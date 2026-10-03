# Validação executada

Data: 03/10/2026. macOS arm64, Maven 3.9.9, JVM efetiva do Maven: Temurin 17.0.17. Compilação com `release=8`.

| Verificação | Resultado |
|---|---|
| Maven package, reactor dos dois módulos | PASSOU: WAR e JAR gerados |
| Jetty local / portal | PASSOU: HTTP em 127.0.0.1:8080 |
| Batch via compile exec:java | PASSOU: total 350.00, arquivo local, mensagem criada sem envio |
| Smoke HTTP | PASSOU: sessão mantém estado; dois clientes isolados; recibo de 100.00 com imposto 18.00 |
| Entradas inválidas | PASSOU: texto, negativo e NaN retornam 400 |
| YAML corporativo | PASSOU: parse com PyYAML; regex bate na fixture positiva e não na negativa; uma fonte positiva por aplicação |
| Script de análise | PASSOU: checagem de sintaxe bash |
| Engine MTA / findings nativos / seleção de labels | PASSOU no Hub 7.3.2 do OpenShift: tarefas 5/6 concluídas; findings nativos e corporativos confirmados |
| Deploy em OpenShift / execução com duas réplicas | NÃO EXECUTADO: etapa futura de modernização |
| Demonstração manual de restart | Documentada; não incluída no smoke automatizado |

Neste ambiente foi utilizado `-Dmaven.repo.local=<workspace>/work/m2` para manter o cache Maven em diretório autorizado. Em uma máquina de desenvolvimento convencional, os comandos do README usam o cache padrão. Os downloads vieram do Maven Central.

O teste de regex não valida a semântica do engine MTA nem substitui `mta-cli rules`/análise real. Antes do evento, execute o ensaio da documentação e salve o relatório real com versão e targets. Os resultados reais estão em `results-mta-7.3.2.md` e os JSONs da API em `../evidence/`.
