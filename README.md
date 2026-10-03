# Do legado ao OpenShift — laboratório MTA para SEF-MG

Dois sistemas **fictícios**, pequenos e deliberadamente legados, para uma demonstração de 1 hora. Nenhuma regra, arquitetura, alíquota ou processo aqui representa a SEF-MG real. Não há credenciais nem dados reais. As aplicações não fazem chamadas a banco, SMTP ou endpoints externos: esses endereços são material de análise estática. Dependências e runtime antigos são intencionais; execute este laboratório localmente.

## Aplicações

| Aplicação | Papel no cenário | Empacotamento | Principal discussão |
|---|---|---|---|
| `portal-arrecadacao` | Emite recibo fictício sobre um valor informado | Java 8 / Servlet 3.1 / WAR | Sessão, filesystem, configuração, Java EE → Jakarta |
| `conciliacao-batch` | Soma dois pagamentos sintéticos e gera resumo | Java 8 / JAR executado pelo Maven | Batch → Job, armazenamento, dependências, JavaMail |

O portal é a demo principal. Os dois módulos são aplicações separadas no inventário, com POM pai compartilhado. O vínculo de negócio é conceitual: o batch usa dados sintéticos próprios, não lê recibos do portal. Isso permite executar cada aplicação isoladamente e discutir um futuro contrato de integração.

## Início rápido

Pré-requisitos: JDK 17 e Maven 3.9.x, acesso ao Maven Central na primeira execução. O bytecode gerado usa Java 8 (`release=8`), mas os plugins de build rodam no JDK 17. Use `mvn -version` para conferir a JVM efetiva. Python 3 é necessário somente para o smoke test opcional.

Na raiz deste diretório:

```bash
mvn clean package
mvn -pl portal-arrecadacao jetty:run
```

Abra **http://127.0.0.1:8080/**. Envie o valor `100.00`: será criado um recibo com imposto fictício de `18.00`. Atualize a página para observar o contador por sessão. Jetty escuta somente na interface local. Encerre com Ctrl+C.

Em outro terminal, na mesma raiz:

```bash
mvn -pl conciliacao-batch exec:java
```

O batch imprime total `350.00`, grava `/tmp/sef-mg-demo/conciliacao/resumo.txt` e cria uma mensagem em memória, sem enviá-la. O JAR não é um fat JAR: use `exec:java`, que fornece as dependências.

Arquivos do portal: `/tmp/sef-mg-demo/recibos/recibo-*.txt`. Esses caminhos são **deliberadamente hardcoded** e preparados para macOS/Linux. Em outro sistema, ajuste os caminhos preservando o antipadrão para análise. `/tmp` continua sendo filesystem local efêmero; usar esse diretório não resolve persistência. Duas execuções do batch sobrescrevem o mesmo resumo, outro ponto de discussão.

Os artefatos são `portal-arrecadacao/target/portal-arrecadacao.war` e `conciliacao-batch/target/conciliacao-batch-1.0.0.jar`.

### Verificação funcional

Com o portal ativo e o batch já executado:

```bash
python3 scripts/smoke.py
```

O teste verifica isolamento entre duas sessões, retenção de estado na mesma sessão, recibo com valor correto, rejeição de entrada inválida e saída do batch. Para demonstrar perda de estado, mantenha o navegador aberto, pare e reinicie o portal e atualize a página: o contador recomeça e o último recibo desaparece da sessão, embora o arquivo permaneça no host. Em um pod substituído sem volume persistente, o arquivo também seria perdido.

## Inventory no MTA Hub

1. Cadastre as duas aplicações com os dados de [inventário sugerido](docs/inventory.md). Esse arquivo é uma ficha de cadastro, não um formato CSV de importação dependente de versão.
2. No assessment, discuta proprietário, criticidade, indisponibilidade tolerada, estado e dependências. Trate as respostas como hipóteses do laboratório.
3. Para análise por código-fonte, publique este diretório em um repositório Git acessível pelo Hub e informe o caminho da aplicação (`portal-arrecadacao` ou `conciliacao-batch`) conforme os campos da versão instalada. Preserve o POM pai na raiz do checkout. A publicação não é necessária para análise local por CLI.
4. Alternativamente, envie o WAR do portal para análise binária. **A regra corporativa deste laboratório procura arquivos `.java` e exige análise de fonte**; não prometa o mesmo resultado para WAR/JAR.
5. Analise primeiro o portal. Classifique os findings, registre decisões e inclua as duas aplicações em ondas de modernização. Uma dependência identificada pelo assessment não é necessariamente inferida pelo analisador.

## Analysis e targets

Ambiente-alvo: **MTA 7.3.2 sobre OpenShift**. Veja a [verificação do ambiente](docs/environment-mta-7.3.2.md). A sintaxe abaixo foi conferida no guia da linha 7.3. Confirme targets/flags na instalação antes da apresentação:

```bash
mta-cli analyze --help
# MTA 7.3.x:
mta-cli analyze --list-targets
bash scripts/analyze.sh portal-arrecadacao
bash scripts/analyze.sh conciliacao-batch
```

O script usa `cloud-readiness`, modo `source-only` e as regras corporativas, com um diretório de relatório novo a cada execução. O MTA e seu runtime de análise devem estar previamente instalados; consulte os requisitos de Podman/Docker ou do modo local da sua distribuição. As aplicações não precisam estar rodando durante a análise. Faça download das imagens e ensaie antes da sessão.

Targets sugeridos, **somente se constarem no catálogo instalado**:

| Objetivo | Target/caminho sugerido | Uso |
|---|---|---|
| Prontidão para containers | `cloud-readiness` | Primeira análise: disco local, IP fixo e sessão |
| Migração de namespace Java EE | `jakarta-ee` | Segunda análise: Servlet/JavaMail e descritores |
| Adoção de EAP 8 | `eap8` | Quando EAP for a escolha real de runtime |
| Atualização do JDK | `openjdk17` | Análise complementar; nem todo código Java 8 gera issue |

Exemplo: `MTA_TARGET=jakarta-ee bash scripts/analyze.sh portal-arrecadacao`.

Não selecione Quarkus apenas por ser um destino possível: estas aplicações não são Spring Boot. OpenShift é plataforma; escolha separadamente o runtime e a estratégia de modernização. Para dependências e uma análise mais completa, repita o comando do script com `--mode full`, garantindo resolução do POM pai e acesso ao Maven. O modo rápido não substitui essa etapa.

Para conduzir a sessão, use a **[apresentação integrada de 60 minutos, com demo de 25 minutos](docs/apresentacao-integrada-60-min.md)**. O [guia detalhado de operação e falas](docs/guia-apresentador.md) é material complementar de ensaio.

Veja [findings esperados](docs/findings.md), [roteiro de 60 minutos](docs/demo-60-min.md) e [backlog de correção](docs/remediation.md).

## Regra corporativa

`rules/sef-corporativo/mainframe.yaml` usa `builtin.filecontent` para encontrar o hostname fictício `mainframe.sef-demo.invalid` em fontes Java. O ID é `sef-mainframe-direto-00001`; o esforço 3 é ilustrativo, não representa dias nem estimativa calibrada. O script adiciona as regras sem desabilitar as regras padrão. O `ruleset.yaml` declara os quatro targets sugeridos para a seleção por labels; ao usar outro target, ajuste essas labels ou rode a regra separadamente sem filtro de target.

A regra tem intencionalmente escopo pequeno e comportamento explicável: uma linha por aplicação contém o hostname. Ela também pode capturar comentários e não detecta construção dinâmica do hostname, propriedades ou variáveis externas. Portanto, demonstra governança automatizada, não comprova que o acoplamento foi removido. A categoria `mandatory` representa a política **fictícia** da demo.

Exercício: em uma cópia/branch, altere o hostname para `api.sef-demo.invalid` e repita a análise. O finding corporativo deve desaparecer; isso prova apenas que a condição textual deixou de existir. A correção real inclui adoção do gateway e revisão do contrato. Preserve os demais findings para mostrar a redução incremental. Há fixtures positiva/negativa em `rule-fixtures/`; não analise a raiz inteira, para não incluir exemplos e documentação no resultado.

## Evolução

A baseline legada fica intacta. Faça um commit inicial ao importar o projeto e crie uma branch de remediação; não há versão modernizada completa neste pacote. O backlog descreve critérios para construir e comparar essa versão depois. Atualizar `javax.servlet` para `jakarta.servlet` exige também atualizar API, descritor e servidor; uma troca textual isolada quebra o deploy no Jetty 9.

## Referências e validação

- [CLI MTA 7.3: modos, flags e relatórios](https://docs.redhat.com/en/documentation/migration_toolkit_for_applications/7.3/html-single/cli_guide/index).
- [Interface do MTA 7.3](https://docs.redhat.com/en/documentation/migration_toolkit_for_applications/7.3/html-single/user_interface_guide/user_interface_guide).
- [Providers e condições de regras](https://docs.redhat.com/en/documentation/migration_toolkit_for_applications/8.0/html/configuring_and_using_rules_for_an_mta_analysis/rules-prov-cond).

Consulte `docs/validation.md` para testes locais e [resultados reais no MTA 7.3.2](docs/results-mta-7.3.2.md) para as análises concluídas no cluster. O mapa de findings esperados é planejamento; o relatório real registra o que foi e o que não foi detectado. A quantidade, severidade e IDs nativos dependem da versão e dos rulesets selecionados. Dependências antigas não implicam automaticamente findings de vulnerabilidade; MTA não substitui análise de composição de software.
