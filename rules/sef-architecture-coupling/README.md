# Acoplamento arquitetural da architecture-demo

Ruleset dedicado à decomposição do monólito, com 18 regras `potential` e mensagens
acionáveis. Não pressupõe que todo acoplamento seja um erro nem que a aplicação
precise virar microserviços. Os esforços são estimativas de triagem, não prazos.

## Mapeamento encontrado

Prefixo comum: `br.gov.mg.sef.fiscal`. São pacotes de um único projeto Maven/JAR,
não módulos Maven independentes. Inspeção do código e `jdeps` confirmaram oito arestas:

| Origem | Destino | Evidência no código | Política |
|---|---|---|---|
| contribuinte | debito | `ContribuinteService.java:10`, `new DebitoService().quitar(...)` | Rever contrato e instanciação |
| debito | arrecadacao | `DebitoService.java:9`, `new ArrecadacaoService().registrarPagamento(...)` | Rever pagamento e consistência |
| arrecadacao | contribuinte | `ArrecadacaoService.java:10`, `new Contribuinte(nome)` | Separar modelo de domínio e contrato |
| arrecadacao | notificacao | `ArrecadacaoService.java:12`, `new NotificacaoService().enviar(...)` | Porta de notificação ou evento |
| contribuinte | shared | `LegacyAudit.registrar(...)` | Permitida |
| debito | shared | `LegacyAudit.registrar(...)` | Permitida |
| arrecadacao | shared | `LegacyAudit.registrar(...)` | Permitida |
| notificacao | shared | `LegacyAudit.registrar(...)` | Permitida |

O ciclo é **contribuinte → debito → arrecadacao → contribuinte**. A última aresta
é uma dependência do modelo, não uma chamada de volta ao serviço. Não há evidência
de recursão infinita. `shared` não referencia nenhum domínio.

## Cobertura

- **12 regras `sefmg-coupling-<origem>-to-<destino>-001`**: todas as direções entre os
  quatro domínios. Referências ao pacote de destino e seus tipos internos passam
  pela análise Java. Isso inclui services, repositories/DAO, implementações e modelos;
  não depende do sufixo da classe. As mensagens das quatro arestas atuais são específicas.
- **3 regras `sefmg-construction-*`**: construção direta dos serviços concretos
  `DebitoService`, `ArrecadacaoService` e `NotificacaoService` fora de seus domínios.
- **1 regra `sefmg-model-arrecadacao-to-contribuinte-001`**: uso do tipo `Contribuinte`
  em Arrecadação, com recomendação de identificador/DTO de contrato.
- **1 regra `sefmg-cycle-contribuinte-debito-arrecadacao-001`**: conjunção das três
  direções, cada uma com seu próprio escopo de arquivos. Só deve disparar quando
  todas estiverem presentes. Não é um algoritmo geral de descoberta de ciclos.
- **1 regra `sefmg-shared-to-domain-001`**: impede que o pacote comum passe a depender
  de um domínio. Não deve produzir issue no código atual.

Não existem repositories, DAOs nem subpacotes `internal`/`impl` na demo atual.
Novos acessos a esses tipos em outro domínio serão capturados pelas regras direcionais;
nenhum incidente desses tipos é alegado como existente. Referências internas ao mesmo
domínio e domínio → shared não são proibidas. Uma futura API entre domínios não fica
automaticamente permitida: a equipe deve decidir a exceção e ajustar a política.

## Execução

Na raiz do repositório, com MTA CLI e seu provider Java/runtime configurados.
Validado com MTA 8.3.0 e JDK 24. O JDT LS instalado requer Java 21 ou superior,
mesmo que a aplicação use Java 17. No macOS, autorize o executável oficial
`java-external-provider` em Privacidade e Segurança se o Gatekeeper o bloquear.

Neste computador, selecione o JDK apenas para a análise:

```bash
ruby scripts/validate-architecture-rules.rb
MTA_JAVA_HOME=/opt/homebrew/opt/openjdk/libexec/openjdk.jdk/Contents/Home \
  bash scripts/analyze-architecture-mta.sh
```

Opcionalmente, informe um diretório novo de saída:

```bash
bash scripts/analyze-architecture-mta.sh /tmp/sef-coupling-report
```

O script recusa sobrescrever um diretório existente e usa, por padrão,
`architecture-demo/target/mta-coupling-<data>-<pid>`. Não usa `--target` nem mistura
regras padrão de migração com as regras de fronteira de domínio. Comando equivalente:

```bash
mta-cli analyze \
  --input "$PWD/architecture-demo" \
  --output /tmp/sef-coupling-report \
  --mode source-only \
  --rules "$PWD/rules/sef-architecture-coupling" \
  --enable-default-rulesets=false
```

Abra o relatório HTML gerado no diretório de saída (normalmente `static-report/index.html`)
e procure os IDs `sefmg-coupling-*`, `sefmg-construction-*`, `sefmg-model-*` e
`sefmg-cycle-*`. O resultado estruturado costuma ser `output.yaml`; confira os arquivos
produzidos pela sua versão. Incidentes devem apontar arquivos/linhas das referências,
não apenas a existência dos arquivos de origem.

Para comparar com o grafo já existente:

```bash
bash scripts/analyze-architecture.sh --no-open
```

## Resultados esperados e testes de aceitação

No código atual, espera-se que **9 IDs de regra** tenham ocorrências: as quatro direções
da tabela, três construções diretas, o modelo compartilhado e o ciclo. O número de
incidentes pode variar com a versão do provider e a contagem de imports/usos. Esses
9 IDs foram confirmados no MTA 8.3.0: 12 incidentes, incluindo três evidências
na regra do ciclo e duas na regra do modelo.

As demais oito direções e a regra shared → domínio devem ficar sem violações.
A regra de ciclo e as regras específicas repetem evidências das regras direcionais;
não some seus esforços como tarefas independentes.

Em uma cópia da aplicação, os testes de aceitação são:

| Alteração controlada | Resultado esperado |
|---|---|
| Manter apenas dependências locais e referências a `shared` | Nenhuma das 18 regras dispara |
| Remover import e uso de `Contribuinte` em `ArrecadacaoService` | Some a direção arrecadacao → contribuinte, a regra de modelo e a de ciclo; demais arestas persistem |
| Referenciar um `DebitoRepository` de contribuinte | Dispara contribuinte → debito; orientar delegação ao dono dos dados |
| Referenciar `debito.internal.Calculadora` de arrecadacao | Dispara arrecadacao → debito |
| Referenciar um domínio em `LegacyAudit` | Dispara shared → domínio |
| Usar nome totalmente qualificado em vez de import | Limitação observada no provider 8.3: as regras genéricas não detectaram os casos testados; revisar também com jdeps |
| Manter uma referência somente em comentário | Não deve criar incidente Java |
| Remover um pacote de origem inteiro | Suas regras direcionais não devem vazar para arquivos de outros domínios |

## Validação realizada e limites

Em 2026-10-06: YAML carregado com parser Psych/Ruby, chaves duplicadas rejeitadas,
18 IDs únicos, campos e encadeamentos checados; script shell verificado com `bash -n`.
Os oito relacionamentos foram confirmados no `jdeps` do JAR disponível e nas fontes.
O validador é uma checagem estrutural deste ruleset, não um schema oficial do MTA.

A análise integrada foi concluída com **MTA CLI 8.3.0 e JDK 24** em 2026-10-06.
A execução anterior esbarrou no Gatekeeper e no JAVA_HOME apontando para Java 17.
Após resolver o ambiente, foi corrigido o encadeamento para pacotes com vários arquivos.

Testes integrados: a aplicação original gerou 9 IDs/12 incidentes; remover a aresta
Arrecadação → Contribuinte eliminou o ciclo e o modelo; somente dependências locais/shared
geraram zero issues; imports de DebitoRepository e debito.internal.Calculadora geraram
somente as duas direções esperadas. Referências totalmente qualificadas sem import
não foram detectadas nos casos testados; essa cobertura não é garantida pelo ruleset.

O campo `filepaths` contém uma lista de um elemento, com uma seção Mustache sobre
`origin.extras.filepaths` (ou o alias correspondente). Essa é a lista original exportada
por `builtin.file`. O provider 8.3 divide por espaços o primeiro elemento; a concatenação
preserva todos os caminhos. Referenciar diretamente `origin.filepaths` truncava a busca
no pacote Contribuinte. O validador estrutural verifica o formato corrigido.
Use um checkout **sem espaços no caminho**, devido a essa limitação do provider.

A sintaxe geral segue os mecanismos do MTA, mas o comportamento deste encadeamento foi
validado em **8.3**, não homologado em 7.3. Revalide ao trocar a versão do provider.

Os seletores assumem a convenção atual `src/main/java/br/gov/mg/sef/fiscal/<dominio>/`.
Testes e fontes fora desse layout ficam fora do escopo. Se mover classes sem preservar
os caminhos de pacote, atualize os seletores. Novos domínios exigem novas regras.
O mecanismo não captura SQL compartilhado, reflexão, configuração nem chamadas dinâmicas.
Referências em imports podem precisar de revisão para distinguir uso efetivo de import
não utilizado. Confirme ciclos no bytecode com `jdeps`.

Referência: [Red Hat MTA 7.3 — Rules Development Guide](https://docs.redhat.com/en/documentation/migration_toolkit_for_applications/7.3/html-single/rules_development_guide/index),
seções de Java provider, chaining, condições lógicas e rulesets.
