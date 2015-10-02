docker-build:
	sbt assembly
	docker build -t danpersa/remindmetolive-scala:latest .

docker-push:
	docker push danpersa/remindmetolive-scala

docker-run:
	docker run -p 8080:8080 -t danpersa/remindmetolive-scala:latest