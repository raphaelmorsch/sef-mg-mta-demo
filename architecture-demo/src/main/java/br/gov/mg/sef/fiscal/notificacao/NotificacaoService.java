package br.gov.mg.sef.fiscal.notificacao;

import br.gov.mg.sef.fiscal.shared.LegacyAudit;

public class NotificacaoService {
    public void enviar(String nome) {
        LegacyAudit.registrar("Notificacao enviada para " + nome);
    }
}
