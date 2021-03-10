node {
	checkout scm
	docker.withRegistry("https://registry.hub.docker.com/","dockerhub"){
	def customImage= docker.build("namsing1/helidon-quickstart-se")
	customImage.push()
	}
}
