# Fichas para inventory/assessment

Todos os valores são escolhas didáticas, a validar no laboratório.

| Campo | Portal | Batch |
|---|---|---|
| Nome | Portal de arrecadação — DEMO | Conciliação — DEMO |
| Responsável | Equipe fictícia de arrecadação | Equipe fictícia de integração |
| Área | Atendimento | Retaguarda |
| Criticidade | Alta (hipótese) | Média (hipótese) |
| Tags sugeridas | Java; Java EE; Web; Stateful | Java; Batch; Integração |
| Código | portal-arrecadacao | conciliacao-batch |
| Estado | HttpSession e arquivos locais | Arquivo de resumo local |
| Integração declarada | Cadastro/mainframe (simulados) | Oracle/SMTP/mainframe (simulados) |
| Estratégia inicial | Replatform + refactor seletivo | Replatform como Job + refactor de I/O |
| Onda | 2: portal após decisões de estado | 1: piloto de Job, com entrada sintética |

Perguntas: quem é dono do dado? O recibo pode ser perdido? Como evitar emissão duplicada? O batch pode ser reexecutado sem duplicar resultados? Que indisponibilidade é tolerada? Quem oferece a API substituta do mainframe?

Dependência de negócio potencial: portal → eventos de arrecadação → conciliação. Isso não está implementado. Não apresentar como descoberta estática. A criticidade pode inverter a ordem das ondas; poucos story points não significam baixo risco de negócio.
