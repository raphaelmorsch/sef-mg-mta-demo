# Ambiente de demonstração — MTA 7.3.2

Verificado em 03/10/2026:

- Operador `mta-operator.v7.3.2`: `Succeeded`.
- Namespace `mta`; Hub e UI: `Running`, prontos, sem reinícios no momento da consulta.
- [MTA do laboratório](https://mta-mta.apps.cluster-7vkrl.dyn.redhatworkshops.io).
- API do Hub acessível; inventário existente com 10 aplicações.
- [Repositório publicado no Gitea](https://gitea.apps.cluster-7vkrl.dyn.redhatworkshops.io/lab-user/sef-mg-mta-demo).
- Aplicação 11: SEF-MG DEMO - Portal de arrecadacao; caminho `portal-arrecadacao`.
- Aplicação 12: SEF-MG DEMO - Conciliacao batch; caminho `conciliacao-batch`.
- Ambas usam branch `main`; regra corporativa via mesmo repositório, caminho `rules/sef-corporativo`.
- Targets selecionados na interface: Containerization (`cloud-readiness`) e Jakarta EE 9 (`jakarta-ee`); modo Source code, escopo Application and internal dependencies.
- Tarefas de análise concluídas: 5 (portal) e 6 (batch), ambas `Succeeded`. Veja [resultados reais](results-mta-7.3.2.md).

## Como repetir a configuração

1. Publicar os fontes em um repositório próprio no Gitea do laboratório.
2. Criar as duas aplicações da demo, preservando os registros existentes.
3. Informar os caminhos de módulos e conferir resolução do POM pai.
4. Rodar análise de fonte com target Cloud readiness e regra corporativa adicional em Advanced. Alternativamente, cadastrar um Custom migration target com a regra.
5. Conferir os targets disponíveis antes de acrescentar Jakarta/EAP 8.
6. Guardar os findings reais e ajustar o roteiro conforme os resultados.

Credenciais não estão neste pacote. O acesso via `oc` utilizou uma configuração temporária separada da configuração pessoal; a API do cluster exigiu exceção local de validação TLS por cadeia de certificado não reconhecida. A rota HTTPS do Hub respondeu com verificação TLS normal.

[Guia de interface 7.3](https://docs.redhat.com/en/documentation/migration_toolkit_for_applications/7.3/html-single/user_interface_guide/user_interface_guide) · [Guia de CLI 7.3](https://docs.redhat.com/en/documentation/migration_toolkit_for_applications/7.3/html-single/cli_guide/index)
