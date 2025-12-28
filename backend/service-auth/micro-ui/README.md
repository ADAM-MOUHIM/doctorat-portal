# Superadmin
# Cr?e le superadmin UNE SEULE FOIS via Postman/curl:
# POST http://localhost:8080/accounts
# {
#   "username": "superadmin",
#   "email": "sa@example.com",
#   "password": "ChangeMe123!",
#   "primaryRole": "SUPERUSER"
# }
# Si un SUPERUSER existe d?j?, l'API renverra une erreur.
# Seul le SUPERUSER peut cr?er des comptes ADMIN (en ?tant authentifi? avec son JWT).
# Les r?les DOCTORANT/DIRECTEUR peuvent s'inscrire via l'UI (register) ou via l'API.
