# Demo simples de dependencias arquiteturais

Projeto Maven independente, Java 17+, sem bibliotecas externas, banco ou servidor.
As dependencias entre dominios sao deliberadas e nao sao uma recomendacao de design.

## Apresentacao

```bash
cd /Users/raphaelmorsch/git/sef-mg-mta-demo
./scripts/analyze-architecture.sh
```

O script compila incrementalmente, gera DOT e SVG e abre o SVG no aplicativo padrao.
Para gerar sem abrir: `./scripts/analyze-architecture.sh --no-open`.
Na primeira compilacao Maven pode precisar baixar plugins. Execute antes da apresentacao.

## Passo a passo manual (na raiz do repositorio)

```bash
export JAVA_HOME="$(/usr/libexec/java_home -v 17)"
export PATH="$JAVA_HOME/bin:$PATH"
java -version
jdeps --version
dot -V
mvn -f architecture-demo/pom.xml package
java -jar architecture-demo/target/architecture-demo-1.0.0.jar
mkdir -p architecture-demo/target/architecture
jdeps -verbose:package -filter:package \
  --regex 'br\.gov\.mg\.sef\.fiscal\..*' \
  --dot-output architecture-demo/target/architecture \
  architecture-demo/target/architecture-demo-1.0.0.jar
sed 's/ (architecture-demo-1\.0\.0\.jar)//g; s/br\.gov\.mg\.sef\.fiscal\.//g' \
  architecture-demo/target/architecture/architecture-demo-1.0.0.jar.dot \
  > architecture-demo/target/architecture/packages.dot
dot -Tsvg -Grankdir=LR -Nshape=box \
  architecture-demo/target/architecture/packages.dot \
  -o architecture-demo/target/architecture/dependencies.svg
open architecture-demo/target/architecture/dependencies.svg
```

`jdeps` faz parte do JDK; nao requer instalacao separada. Se faltar Graphviz: `brew install graphviz`.

## Como explicar

Cada caixa representa um package; A → B significa que o bytecode de A referencia tipos de B.
O ciclo e contribuinte → debito → arrecadacao → contribuinte. A ultima seta e uma referencia
 ao modelo Contribuinte: ciclo de packages nao significa recursao infinita em execucao.
Arrecadacao tambem usa notificacao; os quatro dominios usam shared (LegacyAudit).
Sao oito arestas internas esperadas. Shared nao depende dos outros dominios.

Fala sugerida: “Os nomes sugerem dominios separados, mas o codigo ainda os conecta.
Extrair Arrecadacao exige tratar as referencias a Contribuinte, Notificacao e Shared,
assim como o consumidor Debito. Renomear packages nao cria servicos independentes.”

O SVG usa `packages.dot`, derivado do DOT original do JAR. O script remove o sufixo
do JAR dos destinos para unificar os nos e abrevia o prefixo comum dos packages.
As oito dependencias sao preservadas; o DOT original tambem fica disponivel. `summary.dot` resume
arquivos/modulos e nao e o grafico adequado para mostrar o ciclo interno deste unico JAR.
O filtro omite java.* para facilitar a leitura. Esta e uma analise estatica de bytecode:
nao revela chamadas por reflexao, SQL, configuracao, trafego ou comportamento em runtime,
nem determina sozinho os limites ideais de microservicos.

Saidas em `target/`: classes compiladas, JAR, metadados Maven e, em `target/architecture/`,
`summary.dot`, `architecture-demo-1.0.0.jar.dot`, `packages.dot` e `dependencies.svg`.

## Issues arquiteturais no MTA

O ruleset [sef-architecture-coupling](../rules/sef-architecture-coupling/README.md)
transforma as dependências desta mesma aplicação em issues de desacoplamento:
referências entre domínios, instanciação de services concretos, uso de modelo
de outro domínio e o ciclo conhecido. Dependências domínio → shared são permitidas.

Na raiz do repositório:

```bash
ruby scripts/validate-architecture-rules.rb
bash scripts/analyze-architecture-mta.sh
```

Consulte a documentação do ruleset para cobertura, resultados esperados,
limites e situação da validação integrada com o provider Java.
