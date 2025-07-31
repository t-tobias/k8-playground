
# Traefik via Helm installieren

helm repo add traefik https://helm.traefik.io/traefik
helm repo update

helm upgrade traefik traefik/traefik `
  --namespace kube-system `
  -f traefik-values.yaml
