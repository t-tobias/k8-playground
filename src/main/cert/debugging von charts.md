helm search repo traefik/traefik --versions


#legt ein chart an damit man es sich anschauen kann
helm pull traefik/traefik --version 36.1.0 --untar

#Manifest aus dem K8 holen und mit chart verleichen was kam wirklich an
helm get manifest traefik -n kube-system > manifest-active.yaml

# Welche paramter wurden festgelegt für Chart
helm get values traefik -n kube-system

#Install mit debug hier sieht man was genau passieren wird.
 helm install traefik traefik/traefik --version 36.1.0 --namespace kube-system --dry-run --debug -f .\traefik-values.yaml
 
#local prüfen wie ein chart gerendert werden wird.

helm template traefik traefik/traefik --namespace kube-system -f .\traefik-values.yaml > rendered.yaml 
 