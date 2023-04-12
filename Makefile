uberjar:
	lein uberjar

deploy: uberjar
	scp target/mt2-*-standalone.jar app.melt:mt2/mt2.jar && \
	ssh app.melt sudo systemctl restart mt2 && \
	ssh app.melt systemctl status mt2

clean:
	${RM} -rf target
	${RM} *~

