#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
command -v mta-cli >/dev/null || { echo "Instale mta-cli e configure o provider Java/runtime." >&2; exit 1; }
# Permite selecionar o JDK do analisador sem alterar a configuração global.
if [[ -n "${MTA_JAVA_HOME:-}" ]]; then
  export JAVA_HOME="$MTA_JAVA_HOME"
  export PATH="$JAVA_HOME/bin:$PATH"
fi
OUTPUT="${1:-$ROOT/architecture-demo/target/mta-coupling-$(date +%Y%m%d-%H%M%S)-$$}"
# Caminho absoluto também quando o usuário fornece um diretório relativo.
[[ "$OUTPUT" = /* ]] || OUTPUT="$PWD/$OUTPUT"
if [[ -e "$OUTPUT" ]]; then
  echo "O diretório de saída já existe; escolha outro: $OUTPUT" >&2
  exit 2
fi
mta-cli analyze \
  --input "$ROOT/architecture-demo" \
  --output "$OUTPUT" \
  --mode source-only \
  --rules "$ROOT/rules/sef-architecture-coupling" \
  --enable-default-rulesets=false
printf '\nRelatório MTA: %s\n' "$OUTPUT"
