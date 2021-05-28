#!/bin/sh
if [ -z "$1" ]; then
  echo "usage $0 <version>"
  exit
fi

SED='/bin/sed'
if [ -e ${HOMEBREW_PREFIX}/bin/gsed ]; then
  SED=${HOMEBREW_PREFIX}/bin/gsed
fi

${SED} -E -i.bak "s/\(defproject mt2.*$/\(defproject mt2 \"$1\"/" project.clj
${SED} -E -i.bak "s/\(def version .*$/\(def version \"$1\"\)/" src/mt2/handler/mt2.clj

