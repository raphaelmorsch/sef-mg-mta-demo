# Backlog para versão modernizada

Preservar esta baseline em commit/branch. Em cada etapa: alterar uma preocupação, buildar, testar comportamento e reanalisar com a mesma versão/targets/regras. Guardar relatórios separados para comparação. Menos findings não comprova equivalência funcional.

| Ordem | Alteração | Critério de aceite |
|---|---|---|
| 1 | Externalizar URLs e alíquota, validar configuração no startup | Mudar configuração sem recompilar; falhar com mensagem clara se inválida |
| 2 | Encaminhar mainframe via API institucional fictícia | Contrato testado com stub; timeout e tratamento de falha; sem endpoint direto |
| 3 | API de recibos e armazenamento durável; cálculo decimal | Recibo recuperável após restart; valor correto e idempotência |
| 4 | Remover dependência de sessão ou adotar store externo | Duas réplicas retornam o mesmo estado necessário, inclusive após restart |
| 5 | Migrar APIs Java EE e bibliotecas, selecionar runtime suportado | Build e deploy no runtime Jakarta escolhido; smoke e testes de contrato passam |
| 6 | Empacotar imagem e criar manifests OpenShift | UID arbitrário, porta não privilegiada, filesystem raiz read-only quando aplicável, probes, requests/limits e logs stdout |
| 7 | Batch como Job/CronJob, saída durável e idempotência | Reexecução sem duplicação; concorrência controlada; exit code em falha; agenda explícita |

ConfigMap é adequado para configuração não sensível; credenciais, se futuramente necessárias, devem vir de Secrets ou gerenciador apropriado. Não usar armazenamento temporário como substituto de persistência. Afinidade de sessão isoladamente não resolve perda de estado em falha.

Alternativas para decisão posterior: manter estilo Servlet num runtime Jakarta; ou refatorar para Quarkus/EAP com API e armazenamento externo. Não incluir ambos na primeira demo: escolher um destino, validar o comportamento e medir o que resta.

Não incluímos deploy OpenShift neste baseline: subir o legado sem resolver persistência/estado não conclui a migração. Uma próxima versão pode ter manifests e testes de duas réplicas como parte do aceite.
