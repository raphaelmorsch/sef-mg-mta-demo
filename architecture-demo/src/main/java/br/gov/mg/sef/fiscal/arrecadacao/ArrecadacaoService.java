package br.gov.mg.sef.fiscal.arrecadacao;

import br.gov.mg.sef.fiscal.contribuinte.Contribuinte;
import br.gov.mg.sef.fiscal.notificacao.NotificacaoService;
import br.gov.mg.sef.fiscal.shared.LegacyAudit;

public class ArrecadacaoService {
    public void registrarPagamento(String nome) {
        // Dependencia deliberada do modelo de outro dominio: fecha o ciclo de packages.
        Contribuinte contribuinte = new Contribuinte(nome);
        LegacyAudit.registrar("Pagamento registrado para " + contribuinte.nome());
        new NotificacaoService().enviar(contribuinte.nome());
    }
}
