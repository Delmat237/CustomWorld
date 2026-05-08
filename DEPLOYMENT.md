# Guide de deploiement CustomWorld

Ce guide explique comment deployer le backend CustomWorld sur un VPS avec Docker, DuckDNS, Nginx et HTTPS via Certbot.

## Architecture

```text
Utilisateur
  -> https://customworld.duckdns.org
  -> Nginx :80/:443
  -> API Spring Boot Docker :127.0.0.1:8081
  -> PostgreSQL Docker :5432, reseau Docker interne
```

## Utilite des composants

- **Docker Compose** lance l'API Spring Boot et PostgreSQL avec une configuration reproductible.
- **DuckDNS** fournit un nom de domaine gratuit qui pointe vers l'adresse IP du VPS.
- **Nginx** sert de reverse proxy public. Il recoit les requetes HTTP/HTTPS et les transmet a l'API sur le port `8081`.
- **Certbot / Let's Encrypt** genere et renouvelle automatiquement le certificat HTTPS.

## Prerequis

- Un VPS Ubuntu/Debian avec acces root ou sudo.
- Docker et Docker Compose installes.
- Ports ouverts sur le VPS : `80`, `443`, et `22` pour SSH.
- Un sous-domaine DuckDNS, par exemple `customworld.duckdns.org`.

## 1. Lancer l'application Docker

Depuis le dossier du projet :

```bash
cd ~/CustomWorld
docker compose up --build -d
```

Verifier que les conteneurs tournent :

```bash
docker ps
```

Verifier les logs de l'API :

```bash
docker logs -f customworld-api
```

L'API doit demarrer sur le port `8081`.

## 2. Configuration Docker recommandee

Dans `docker-compose.yml`, l'API peut etre exposee seulement en local, car Nginx sera le point d'entree public :

```yaml
ports:
  - "127.0.0.1:8081:8081"
```

Pour PostgreSQL, il est preferable de ne pas exposer le port publiquement. Le service `api` peut deja joindre la base via le reseau Docker avec :

```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/customworld
```

Apres modification :

```bash
docker compose down
docker compose up --build -d
```

## 3. Configurer DuckDNS

Creer un sous-domaine sur :

```text
https://www.duckdns.org
```

Exemple :

```text
customworld.duckdns.org
```

Sur le VPS :

```bash
mkdir -p ~/duckdns
nano ~/duckdns/duck.sh
```

Contenu du fichier, en remplacant `customworld` et `TON_TOKEN_DUCKDNS` :

```bash
echo url="https://www.duckdns.org/update?domains=customworld&token=TON_TOKEN_DUCKDNS&ip=" | curl -k -o ~/duckdns/duck.log -K -
```

Rendre le script executable et le lancer :

```bash
chmod +x ~/duckdns/duck.sh
~/duckdns/duck.sh
cat ~/duckdns/duck.log
```

Resultat attendu :

```text
OK
```

Verifier que le domaine pointe vers le VPS :

```bash
ping -c 3 customworld.duckdns.org
```

Ajouter la mise a jour automatique toutes les 5 minutes :

```bash
crontab -e
```

Ajouter :

```cron
*/5 * * * * /root/duckdns/duck.sh >/dev/null 2>&1
```

## 4. Installer Nginx

```bash
sudo apt update
sudo apt install nginx -y
sudo systemctl enable nginx
sudo systemctl start nginx
```

Si `ufw` est utilise :

```bash
sudo ufw allow OpenSSH
sudo ufw allow 'Nginx Full'
sudo ufw enable
sudo ufw status
```

## 5. Configurer Nginx comme reverse proxy

Creer le fichier :

```bash
sudo nano /etc/nginx/sites-available/customworld
```

Configuration HTTP :

```nginx
server {
    listen 80;
    server_name customworld.duckdns.org;

    client_max_body_size 10M;

    location / {
        proxy_pass http://127.0.0.1:8081;
        proxy_http_version 1.1;

        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Activer le site :

```bash
sudo ln -s /etc/nginx/sites-available/customworld /etc/nginx/sites-enabled/customworld
sudo nginx -t
sudo systemctl reload nginx
```

Tester :

```bash
curl http://customworld.duckdns.org/swagger-ui/index.html
```

## 6. Activer HTTPS avec Certbot

Installer Certbot :

```bash
sudo apt install certbot python3-certbot-nginx -y
```

Generer le certificat :

```bash
sudo certbot --nginx -d customworld.duckdns.org
```

Certbot modifie automatiquement la configuration Nginx pour ajouter HTTPS et la redirection HTTP vers HTTPS.

Tester le renouvellement automatique :

```bash
sudo certbot renew --dry-run
```

Tester le site en HTTPS :

```bash
curl https://customworld.duckdns.org/swagger-ui/index.html
```

## 7. URLs utiles

- API : `https://customworld.duckdns.org/api`
- Swagger UI : `https://customworld.duckdns.org/swagger-ui/index.html`
- Logs API : `docker logs -f customworld-api`
- Logs Nginx :

```bash
sudo tail -f /var/log/nginx/access.log
sudo tail -f /var/log/nginx/error.log
```

## 8. Commandes de maintenance

Redemarrer l'application :

```bash
docker compose down
docker compose up --build -d
```

Voir l'etat des conteneurs :

```bash
docker ps
```

Redemarrer Nginx :

```bash
sudo nginx -t
sudo systemctl reload nginx
```

Verifier DuckDNS :

```bash
~/duckdns/duck.sh
cat ~/duckdns/duck.log
ping -c 3 customworld.duckdns.org
```

## 9. Depannage rapide

### Le domaine ne repond pas

Verifier que DuckDNS pointe vers l'IP du VPS :

```bash
ping customworld.duckdns.org
```

Verifier les ports ouverts :

```bash
sudo ufw status
```

### Nginx renvoie 502 Bad Gateway

Cela signifie souvent que Nginx ne peut pas joindre l'API.

Verifier que l'API tourne :

```bash
docker ps
docker logs customworld-api
```

Tester localement depuis le VPS :

```bash
curl http://127.0.0.1:8081/swagger-ui/index.html
```

### HTTPS ne fonctionne pas

Verifier Nginx :

```bash
sudo nginx -t
sudo systemctl status nginx
```

Verifier Certbot :

```bash
sudo certbot certificates
sudo certbot renew --dry-run
```

### `ping https://customworld.duckdns.org` ne fonctionne pas

C'est normal. `ping` prend un nom de domaine, pas une URL :

```bash
ping customworld.duckdns.org
```

Pour tester HTTP/HTTPS, utiliser `curl` :

```bash
curl https://customworld.duckdns.org
```

