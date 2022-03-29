#!/bin/sh
if [ -z "$1" ]; then
	echo usage: $0 target/file.jar
	exit 1
fi
BN=`basename $1`
scp $1 app.melt:mt2/ && \
ssh app.melt "(cd mt2 && ln -sf ${BN} mt2.jar)" && \
ssh app.melt sudo systemctl restart mt2 && \
ssh app.melt systemctl status mt2

