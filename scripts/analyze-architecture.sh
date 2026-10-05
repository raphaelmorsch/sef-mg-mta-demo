#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROJECT="$ROOT/architecture-demo"
OUT="$PROJECT/target/architecture"
if [[ -z "${JAVA_HOME:-}" ]]; then
  export JAVA_HOME="$(/usr/libexec/java_home -v 17)"
fi
export PATH="$JAVA_HOME/bin:$PATH"
for tool in java javac jdeps mvn dot; do
  command -v "$tool" >/dev/null || { echo "Ferramenta ausente: $tool" >&2; exit 1; }
done
[[ -x "$JAVA_HOME/bin/jdeps" ]] || { echo "JAVA_HOME deve apontar para um JDK com jdeps." >&2; exit 1; }
# Maven compila incrementalmente e atualiza o JAR quando necessario.
mvn -B -f "$PROJECT/pom.xml" package
mkdir -p "$OUT"
JAR="$PROJECT/target/architecture-demo-1.0.0.jar"
# filter:package preserva as dependencias entre packages do mesmo JAR.
# regex limita os destinos aos cinco packages da demo, retirando java.*.
jdeps -verbose:package -filter:package   --regex 'br\.gov\.mg\.sef\.fiscal\..*'   --dot-output "$OUT" "$JAR"
RAW_DOT="$OUT/architecture-demo-1.0.0.jar.dot"
DOT="$OUT/packages.dot"
# jdeps anexa o nome do JAR aos destinos: unificar IDs revela o ciclo real.
sed 's/ (architecture-demo-1\.0\.0\.jar)//g; s/br\.gov\.mg\.sef\.fiscal\.//g' "$RAW_DOT" > "$DOT"
SVG="$OUT/dependencies.svg"
dot -Tsvg -Grankdir=LR -Gbgcolor=white -Nshape=box   -Nstyle=rounded -Nfontname=Helvetica "$DOT" -o "$SVG"
printf '\nGrafico: %s\nDOT de packages: %s\n' "$SVG" "$DOT"
if [[ "${1:-}" != "--no-open" ]]; then
  open "$SVG"
fi
