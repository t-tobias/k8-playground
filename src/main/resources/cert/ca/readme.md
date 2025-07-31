
#cert revoke and update crl with revoke
openssl ca -config openssl.cnf -revoke ..\client.crt
openssl ca -config openssl.cnf -gencrl -out ca.crl

#create crl
openssl ca -gencrl -config openssl.cnf -out ca.crl

#check crl
openssl crl -in ca.crl -noout -text

#check if cert is revoked
openssl verify -crl_check -CAfile ca.crt -CRLfile ca.crl ..\client.crt

#undo revoke not easy possible but a new index.txt and crl can be created