#!/bin/sh
if [ -z "$1" ]; then
  echo "usage $0 <version>"
  exit
fi


if [ -e /usr/local/bin/gsed ]; then
  # macOS
  SED='/usr/local/bin/gsed'
elif [ -e 'bin/sed' ]; then
  # linux
  SED='/bin/sed'
else
  echo 'not found sed'
fi

${SED} -E -i.bak "s/\(defproject mt2.*$/\(defproject mt2 \"$1\"/" project.clj
${SED} -E -i.bak "s/\(def version.*$/\(def version \"$1\"\)/" src/mt2/handler/mt2.clj

