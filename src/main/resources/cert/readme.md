# Zertifikate generieren

# Erstelle ein Verzeichnis
mkdir certs && cd certs

# CA erstellen
- openssl genrsa -out ca.key 4096
- openssl req -x509 -new -nodes -key ca.key -sha256 -days 3650 -out ca.crt -subj "/CN=TestCA"

# Server Zertifikat
- openssl genrsa -out server.key 4096
- openssl req -new -key server.key -out server.csr -subj "/CN=myapp.localtest.me"
- openssl x509 -req -in server.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out server.crt -days 365 -sha256

# Client Zertifikat
- openssl genrsa -out client.key 4096
- openssl req -new -key client.key -out client.csr -subj "/CN=Client"
- openssl x509 -req -in client.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out client.crt -days 365 -sha256

# Kubernetes Secrets erzeugen
- kubectl create namespace myapp

- kubectl create secret tls myapp-tls-secret `
  --cert=server.crt --key=server.key -n myapp

- kubectl create secret generic myapp-ca-secret `
  --from-file=ca.crt=ca.crt -n myapp

- kubectl create secret generic client-cert-secret `
  --from-file=client.crt=client.crt `
  --from-file=client.key=client.key -n myapp

Alternative workaround without CRD

- kubectl create secret tls myapp-tls-secret `
  --cert=server.crt --key=server.key -n kube-system

- kubectl create secret generic myapp-ca-secret `
  --from-file=ca.crt=ca.crt -n kube-system

- kubectl create secret generic client-cert-secret `
  --from-file=client.crt=client.crt `
  --from-file=client.key=client.key -n kube-system
