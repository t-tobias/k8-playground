


https://docs.nginx.com/nginx-gateway-fabric/overview/gateway-api-compatibility/

kind create cluster --name 01-cluster --config kind-config.yaml

# How to install mtls samples service in kind
kubectl create namespace myapp
mvn clean install
docker build -t example/myapp-k8s:1.0.0 .
kind load docker-image example/myapp-k8s:1.0.0 --name 01-cluster

helm upgrade --install myapp-mtls .\src\main\resources\k8s\helm\mtls\ --namespace default

kubectl apply -f https://github.com/kubernetes-sigs/gateway-api/releases/download/v1.3.0/standard-install.yaml
kubectl apply -f https://raw.githubusercontent.com/nginx/nginx-gateway-fabric/v2.0.2/deploy/crds.yaml
kubectl apply -f https://raw.githubusercontent.com/nginx/nginx-gateway-fabric/v2.0.2/deploy/default/deploy.yaml

kubectl create secret tls myapp-tls --cert=server.crt  --key=server.key -n nginx-gateway
kubectl create secret generic mtls-ca  --from-file=ca.crt=ca.crt -n nginx-gateway

kubectl apply -f .\gateway.yaml  
kubectl apply -f .\route.yaml     
kubectl apply -f .\nginx-gateway-config.yaml    
kubectl apply -f .\refgrant.yaml  
kubectl apply -f .\gatewayclass.yaml 
kubectl rollout restart deployment nginx-gateway -n nginx-gateway

kubectl get crds

# Portforwarding from nginx dataplane pod change hashcode from pod
kubectl port-forward pod/myapp-gateway-nginx-f55c5b6dd-dl5jb 9443:443 -n myapp

# Test if mtls is behind the script must have the client.pfx which have the client.crt and client.key included.
# client.pfx must be existing in current dir.
curl -vk https://myapp.mtls.local:9443/hello --resolve myapp.mtls.local:9443:127.0.0.1 --cert-type P12 --cert client.pfx:123


Result:
* Added myapp.mtls.local:9443:127.0.0.1 to DNS cache
* Hostname myapp.mtls.local was found in DNS cache
*   Trying 127.0.0.1:9443...
* schannel: disabled automatic use of client certificate
* ALPN: curl offers http/1.1
* ALPN: server accepted http/1.1
* Connected to myapp.mtls.local (127.0.0.1) port 9443
* using HTTP/1.x
> GET /hello HTTP/1.1
> Host: myapp.mtls.local:9443
> User-Agent: curl/8.13.0
> Accept: */*
>
* Request completely sent off
< HTTP/1.1 200
< Server: nginx
< Date: Wed, 30 Jul 2025 13:41:26 GMT
< Content-Type: text/plain;charset=UTF-8
< Content-Length: 55
< Connection: keep-alive
<
Hello from Spring Boot at 2025-07-30T13:41:26.177963232* Connection #0 to host myapp.mtls.local left intact

# debugging why it is not working get tls cert from server is it the testCA cert with cn CN=myapp.localtest.me or the nginx default
openssl s_client -connect localhost:9443 -servername myapp.mtls.local -showcerts

#  describe gateway to check if the certs are found
kubectl describe gateway myapp-gateway -n myapp

Login to pod and have a look on the /etc/nginx/secrets there the certs are mounted


Result:

Nginx do not support CRL on gateway api.

___________________________________________________________________________________________________________________________

Envoy https://gateway.envoyproxy.io/docs/tasks/quickstart/
