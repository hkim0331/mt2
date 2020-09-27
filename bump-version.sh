#!/bin/sh
if [ -z "$1" ]; then
  echo "usage $0 <version>"
  exit
fi

sed -E -i.bak "s/\(defproject mt2.*$/\(defproject mt2 \"$1\"/" project.clj
