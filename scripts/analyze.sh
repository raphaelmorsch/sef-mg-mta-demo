#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
app="${1:-portal-arrecadacao}"
case "$app" in portal-arrecadacao|conciliacao-batch) ;; *) echo "Aplicação inválida" >&2; exit 2 ;; esac
command -v mta-cli >/dev/null || { echo "Instale mta-cli e prepare seu runtime de análise conforme README." >&2; exit 1; }
# Confirme estes targets na instalação; sobrescreva com MTA_TARGET.
# Rodar separadamente evita misturar esforço de objetivos de migração distintos.
target="${MTA_TARGET:-cloud-readiness}"
output="reports/${app}-$(date +%Y%m%d-%H%M%S)-${RANDOM}"
mta-cli analyze --input "$PWD/$app" --output "$PWD/$output" --mode source-only \
  --target "$target" --rules "$PWD/rules/sef-corporativo"
