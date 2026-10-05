package br.gov.mg.sef.fiscal.debito;

import br.gov.mg.sef.fiscal.arrecadacao.ArrecadacaoService;
import br.gov.mg.sef.fiscal.shared.LegacyAudit;

public class DebitoService {
    public void quitar(String nome) {
        LegacyAudit.registrar("Quitando debito de " + nome);
        new ArrecadacaoService().registrarPagamento(nome);
    }
}
