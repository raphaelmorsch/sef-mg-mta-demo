#!/usr/bin/env python3
"""Execute com portal na porta 8080 e batch executado; usa somente stdlib."""
import http.cookiejar
import pathlib
import re
import urllib.request
import urllib.error

def client():
    return urllib.request.build_opener(urllib.request.HTTPCookieProcessor(http.cookiejar.CookieJar()))
def get(c):
    return c.open("http://127.0.0.1:8080/", timeout=10).read().decode("utf-8")
a, b = client(), client()
assert "Visitas nesta sessão: 1" in get(a)
assert "Visitas nesta sessão: 2" in get(a)
assert "Visitas nesta sessão: 1" in get(b)
body = a.open("http://127.0.0.1:8080/", data=b"valor=100.00", timeout=10).read().decode("utf-8")
name = re.search(r"Último recibo: (recibo-[^<]+\.txt)", body).group(1)
receipt = pathlib.Path("/tmp/sef-mg-demo/recibos", name).read_text()
assert "base=100.00" in receipt and "imposto=18.00" in receipt
assert name in get(a)
assert "Último recibo: nenhum" in get(b)
for value in (b"valor=invalido", b"valor=-1", b"valor=NaN"):
    try:
        a.open("http://127.0.0.1:8080/", data=value, timeout=10)
        raise AssertionError("Entrada inválida foi aceita")
    except urllib.error.HTTPError as error:
        assert error.code == 400
summary = pathlib.Path("/tmp/sef-mg-demo/conciliacao/resumo.txt").read_text()
assert "registros=2" in summary and "total=350.00" in summary
print("OK: sessões isoladas, recibo persistido localmente, valores e validações, batch.")
