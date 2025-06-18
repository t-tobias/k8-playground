openssl s_client `
  -connect myapp.localtest.me:8443 `
  -cert client.crt `
  -key client.key `
  -CAfile ca.crt `
  -servername myapp.localtest.me
