# Resultados reais — MTA 7.3.2

Executado em 03/10/2026 no OpenShift do laboratório. As tarefas 5 e 6 terminaram em `Succeeded`; a interface mostra `Completed` para ambas as aplicações. Os 10 registros preexistentes não foram alterados.

[Inventário MTA](https://mta-mta.apps.cluster-7vkrl.dyn.redhatworkshops.io/applications) · [Fontes no Gitea](https://gitea.apps.cluster-7vkrl.dyn.redhatworkshops.io/lab-user/sef-mg-mta-demo)

Configuração: Source code (`withDeps=false`), escopo Application and internal dependencies, targets `cloud-readiness` e `jakarta-ee`, tagging habilitado. Regras adicionais em `rules/sef-corporativo`, recuperadas do mesmo repositório. Esta execução combina os dois targets; não confundir o total com o esforço exclusivo de containerização. Os comandos CLI do README permitem ensaiar cada target separadamente.

Commit de fonte analisado: `b0696409a7f2b8e8fb8f9aa0f84c41bb20b65503`. Alterações posteriores de documentação não mudam esse registro. JSONs originais da API estão em `evidence/`.

## Portal de arrecadação

Aplicação 11, análise 2, tarefa 5: **8 tipos de issues, 14 ocorrências, esforço total 16**. Esforço é a pontuação do MTA, não dias.

| Regra | Categoria | Ocorrências | Evidência (arquivo:linha) |
|---|---|---:|---|
| `local-storage-00001` | mandatory | 2 | PortalServlet.java:45, PortalServlet.java:48 |
| `hardcoded-ip-address` | mandatory | 1 | PortalServlet.java:17 |
| `javaee-to-jakarta-namespaces-00044` | mandatory | 2 | web.xml:0, web.xml:2 |
| `javax-to-jakarta-dependencies-00001` | mandatory | 1 | pom.xml:4 |
| `javax-to-jakarta-dependencies-00002` | mandatory | 1 | pom.xml:4 |
| `javax-to-jakarta-import-00001` | mandatory | 5 | PortalServlet.java:6, PortalServlet.java:7, PortalServlet.java:8, PortalServlet.java:9, PortalServlet.java:10 |
| `javaee-to-jakarta-namespaces-00001` | mandatory | 1 | web.xml:2 |
| `sef-mainframe-direto-00001` | mandatory | 1 | PortalServlet.java:18 |
## Conciliação batch

Aplicação 12, análise 1, tarefa 6: **7 tipos de issues, 9 ocorrências, esforço total 15**. Esforço é a pontuação do MTA, não dias.

| Regra | Categoria | Ocorrências | Evidência (arquivo:linha) |
|---|---|---:|---|
| `local-storage-00001` | mandatory | 1 | ConciliacaoBatch.java:26 |
| `mail-00000` | optional | 2 | ConciliacaoBatch.java:7, ConciliacaoBatch.java:8 |
| `hardcoded-ip-address` | mandatory | 1 | ConciliacaoBatch.java:14 |
| `javax-to-jakarta-dependencies-00001` | mandatory | 1 | pom.xml:4 |
| `javax-to-jakarta-import-00001` | mandatory | 2 | ConciliacaoBatch.java:7, ConciliacaoBatch.java:8 |
| `javax-to-jakarta-servlet-00130` | potential | 1 | pom.xml:4 |
| `sef-mainframe-direto-00001` | mandatory | 1 | ConciliacaoBatch.java:16 |

## Interpretação para a demo

- Filesystem local e IP hardcoded foram detectados nas duas aplicações.
- A regra `sef-mainframe-direto-00001` gerou exatamente uma ocorrência por aplicação, no respectivo arquivo Java. O recorte por módulo funcionou para os findings coletados.
- O portal mostra alterações de imports Servlet, dependências e descritor `web.xml` para Jakarta.
- O batch mostra JavaMail e alterações de imports/dependências. O finding `javax-to-jakarta-servlet-00130` descreve `web.xml`, mas apontou para `pom.xml`: é um candidato a falso positivo/imprecisão da regra e requer revisão. Não há `web.xml` no batch.
- A linha `0` indicada em um incidente XML é a localização retornada pelo analisador; use a ocorrência na linha 2 para navegar no arquivo.
- Não houve issue específico de HttpSession, da alíquota embutida ou da configuração `batch.properties` neste conjunto de resultados. Esses tópicos continuam demonstráveis pelo código e pelo comportamento, como revisão arquitetural humana.
- Bibliotecas antigas estão declaradas, mas a execução foi source-only e não produziu um inventário completo de dependências. A descoberta automática de tecnologias também terminou com sucesso; isso não substitui análise completa nem SCA.

## Sequência recomendada no relatório

1. Abrir o portal no inventário (nome começa com `SEF-MG DEMO`) e sua análise concluída.
2. Mostrar `hardcoded-ip-address` e `local-storage-00001` como impactos concretos para OpenShift.
3. Mostrar `javax-to-jakarta-import-00001` e o descritor como mudança de runtime.
4. Abrir `sef-mainframe-direto-00001`, explicar a política fictícia e navegar até a linha.
5. Mostrar o batch para comparar criticidade, estratégia e ondas; discutir o possível falso positivo como exemplo de revisão humana.
6. Executar o exercício de remediação em uma branch e reanalisar, preservando a baseline. A versão modernizada e a análise comparativa ainda são próximas etapas.

