#!/usr/bin/env ruby
# Validação estrutural local; não substitui execução no engine/provider MTA.
require 'yaml'
root = File.expand_path('..', __dir__)
dir = File.join(root, 'rules', 'sef-architecture-coupling')
def check(value, message)
  raise message unless value
end
def no_duplicates(node)
  if node.is_a?(Psych::Nodes::Mapping)
    keys = node.children.each_slice(2).map { |key, _| key.value }
    check(keys.uniq == keys, "Chaves YAML duplicadas: #{keys}")
  end
  (node.children || []).each { |child| no_duplicates(child) }
end
def read_yaml(path)
  no_duplicates(Psych.parse_file(path))
  YAML.safe_load(File.read(path), permitted_classes: [], permitted_symbols: [], aliases: false)
end
def condition(node, aliases = [])
  check(node.is_a?(Hash), 'Condição deve ser um mapa')
  operator = node.keys & %w[and or builtin.file java.referenced]
  check(operator.size == 1, "Condição inválida: #{node.keys}")
  check((node.keys - %w[and or builtin.file java.referenced as from ignore]).empty?, 'Campo de condição desconhecido')
  key = operator.first
  if %w[and or].include?(key)
    children = node.fetch(key)
    check(children.is_a?(Array) && !children.empty?, 'Operador lógico vazio')
    local = aliases + children.map { |child| child['as'] }.compact
    children.each { |child| condition(child, local) }
  else
    fields = node.fetch(key)
    check(fields['pattern'].is_a?(String) && !fields['pattern'].empty?, 'Pattern ausente')
    if key == 'builtin.file'
      Regexp.new(fields['pattern'])
      check(node['ignore'] == true && node['as'], 'Seletor de arquivos precisa de as/ignore')
    else
      check((fields.keys - %w[pattern location filepaths]).empty?, 'Campo Java desconhecido')
      check([nil, 'TYPE', 'CONSTRUCTOR_CALL'].include?(fields['location']), 'Location inesperado')
      check(aliases.include?(node['from']), "Alias não definido: #{node['from']}")
      check(fields['filepaths'] == ["{{##{node['from']}.extras.filepaths}}{{.}} {{/#{node['from']}.extras.filepaths}}"], 'Escopo de arquivos ausente/incorreto')
    end
  end
end
metadata = read_yaml(File.join(dir, 'ruleset.yaml'))
check(metadata['name'] == 'sefmg-architecture-coupling', 'Nome do ruleset inválido')
rules = read_yaml(File.join(dir, 'coupling.yaml'))
check(rules.is_a?(Array) && rules.size == 18, 'Esperadas 18 regras')
ids = rules.map { |rule| rule.fetch('ruleID') }
check(ids.uniq == ids, 'ruleID duplicado')
rules.each do |rule|
  %w[description message].each { |field| check(rule[field].is_a?(String) && !rule[field].empty?, "#{field} ausente") }
  check(rule['category'] == 'potential', 'Categoria inesperada')
  check(rule['effort'].is_a?(Integer) && rule['effort'] > 0, 'Esforço inválido')
  condition(rule.fetch('when'))
end
puts "OK: YAML, chaves únicas, 18 IDs, metadados, condições e encadeamentos."
puts 'Esta checagem não valida resolução Java nem resultados do MTA.'
