#k8 Playground

#Step 1: install kind, helm und docker and k8 
choco install kind kubernetes-cli kubernetes-helm docker-desktop -y


#Step 2: Create new cluster in kind

kind create cluster --name 01-cluster --config kind-config.yaml
kubectl label node 01-cluster-control-plane ingress-ready=true --overwrite
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update
helm install ingress-nginx ingress-nginx/ingress-nginx --namespace ingress-nginx --create-namespace --set controller.hostPort.enabled=true --set controller.service.type=NodePort


Step 3: Check if cluster is there
kubectl get nodes

Steps für update des helm chart

Step1: mvn clean install
Step2: docker build -t example/myapp-k8s:1.0.0 .
Step3: Image in kind laden
 kind load docker-image example/myapp-k8s:1.0.0 --name 01-cluster
Step4: helm updaten
helm upgrade --install myapp .\src\main\k8s\helm\myapp\ --namespace default

helm upgrade --install myapp-mtls .\src\main\k8s\helm\mtls\ --namespace default


Traefik:

auch per helm

Außerdem braucht man CRDs für das Cert und routing handling

kubectl apply -f https://github.com/traefik/traefik/releases/download/v2.11.0/traefik-crds.yaml



Wichtige Stolpersteine:

Immer imagePullPolicy: Never setzen sonst will er immer das image aus dem Docker hub holen und für kind load docker-image nutzen.
Achtung auch nie Ports im kind export die im Browser geblocked werden. Besser 8080 und 8443.


Okay nicht jedes helm update führt ein neustart und ersetzen des Containers durch. Aber hier gäbe es ein Möglichkeit immer zu ersetzen.


 Pro-Tipp: Automatischer Rollout über Chart

Ergänze in deinem Deployment-Template:

spec:
  template:
    metadata:
      annotations:
        rolloutTimestamp: "{{ now | date "2006-01-02T15:04:05Z07:00" }}"

Dann gilt: Jedes Helm-Upgrade führt garantiert zum Pod-Rollout – auch ohne echte Änderung.



#Port forward den nginx auf 8080 sonst keine Zugriff wegen wsl. Also Kind öffnet den 80 und 443 port zum nginx.
kind create cluster --name 01-cluster --config kind-config.yaml
#Tag für nginx auf control plane

kubectl label node 01-cluster-control-plane ingress-ready=true --overwrite

#ingress drauf

kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.9.5/deploy/static/provider/kind/deploy.yaml

kubectl wait --namespace ingress-nginx --for=condition=Ready pod  --selector=app.kubernetes.io/component=controller   --timeout=90s




🧠 Tipp: localtest.me funktioniert ohne Hosts-Datei
Die Domain *.localtest.me wird automatisch auf 127.0.0.1 aufgelöst – kostenlos von readme.io.

✅ Das heißt:
Du musst nichts eintragen, wenn du myapp.localtest.me verwendest – das funktioniert sofort.


Ingress	Die Regel, wie HTTP(S) extern ins Cluster geroutet wird
Ingress Controller	Die Komponente, die diese Regeln ausführt
NGINX Ingress	Der bekannteste Ingress Controller (basierend auf NGINX)



 3 wichtigsten Service-Typen in Kubernetes, die sich auf Zugänglichkeit von außen beziehen:
 
 
 NodePort vs. LoadBalancer vs. Port-Forward
 
 | Typ              | Extern erreichbar? | Externe IP nötig?    | Anwendungsfall                            | Vorteile                         | Nachteile                               |
| ---------------- | ------------------ | -------------------- | ----------------------------------------- | -------------------------------- | --------------------------------------- |
| **NodePort**     | ✅ Ja (fester Port) | ❌ Nein               | Test, Development, Cluster-internes Debug | Schnell, einfach                 | Port muss freigegeben sein, unsicher    |
| **LoadBalancer** | ✅ Ja (Cloud IP)    | ✅ Ja (z. B. AWS ELB) | Produktionsbetrieb auf Cloud (AWS, Azure) | Automatisch öffentliche IP + DNS | Nur auf Cloud, evtl. Kosten             |
| **Port-Forward** | ⚠️ Nur lokal       | ❌ Nein               | Kurzzeitiger Test, Debugging via CLI      | Kein Setup nötig, direkt per CLI | Nur solange CLI läuft, keine Automation |
 
 
Ein Ingress ist ein Reverse Proxy (z. B. Traefik, NGINX), der viele Dienste hinter einer gemeinsamen IP zugänglich macht – z. B. via Hostname oder Pfad.
Er wir nicht unbedingt benötigt um System direkt an den loadbalancer an der Cloud zu binden. 




The p4m projects you can have fun on your train:
Browse .next Development China / porting-resource-collector - medavis Bitbucket - Collect resources by running windows docker container
 
Browse .next Development China / portal-platform-containerization - medavis Bitbucket - Build linux docker containers based on the resource collected in previous project
 
Browse China Dev Ops / AWS-infra-p4m-EKS - medavis Bitbucket - helm chart for running on minkube
 