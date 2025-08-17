# CustomWorld Backend

CustomWorld Backend est une application Spring Boot conçue pour gérer une plateforme de personnalisation d'accessoires (souris, vêtements, sacs, etc.). Elle fournit une API RESTful sécurisée pour quatre types d'utilisateurs : clients, vendeurs, livreurs et administrateurs. L'application supporte l'authentification JWT, le rafraîchissement de tokens, la réinitialisation de mot de passe, l'upload de fichiers, et une architecture scalable avec une base de données PostgreSQL.

## Table des matières
1. [Fonctionnalités](#fonctionnalités)
2. [Structure du projet](#structure-du-projet)
3. [Prérequis](#prérequis)
4. [Installation](#installation)
5. [Configuration](#configuration)
6. [Utilisation](#utilisation)
7. [Documentation de l'API](#documentation-de-lapi)
8. [Tests](#tests)
9. [Déploiement](#déploiement)
10. [Contribuer](#contribuer)
11. [Contact](#contact)
12. [Licence](#licence)

## Fonctionnalités
- **Vue Client** :
    - Consulter les produits disponibles.
    - Passer des commandes avec personnalisation (upload d'images).
    - Consulter l'historique des commandes.
- **Vue Vendeur** :
    - Publier et gérer des produits (avec images).
    - Consulter les commandes associées à leurs produits.
- **Vue Livreur** :
    - Consulter les livraisons assignées.
    - Mettre à jour le statut des livraisons (ex. "Livré").
- **Vue Administrateur** :
    - Gérer les utilisateurs (création, modification, suppression).
    - Valider les produits publiés par les vendeurs.
    - Assigner les commandes aux livreurs.
    - Consulter les statistiques (via endpoints).
- **Sécurité** :
    - Authentification via JWT avec access tokens et refresh tokens.
    - Réinitialisation de mot de passe via email.
    - Rôles utilisateurs : CUSTOMER, VENDOR, DELIVERER, ADMIN.
    - Hachage des mots de passe avec BCrypt.
- **Gestion des fichiers** :
    - Upload et stockage d'images pour les produits et commandes.
- **Notifications** :
    - Envoi d'emails pour l'inscription et la réinitialisation de mot de passe.
    - Envoi de SMS (optionnel, via Twilio) pour les notifications d'inscription.
- **Scalabilité** :
    - Architecture modulaire avec services et repositories.
    - Base de données PostgreSQL avec Spring Data JPA.

## Structure du projet
Le projet suit une architecture standard Spring Boot, organisée comme suit :

```
customworld-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/customworld/
│   │   │       ├── CustomWorldApplication.java
│   │   │       ├── config/
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   ├── FileStorageConfig.java
│   │   │       │   ├── CorsConfig.java
│   │   │       │   └── SwaggerConfig.java
│   │   │       ├── controller/
│   │   │       │   ├── AuthController.java
│   │   │       │   ├── CustomerController.java
│   │   │       │   ├── VendorController.java
│   │   │       │   ├── DeliveryController.java
│   │   │       │   ├── AdminController.java
│   │   │       │   └── FileController.java
│   │   │       ├── dto/
│   │   │       │   ├── request/
│   │   │       │   │   ├── LoginRequest.java
│   │   │       │   │   ├── RegisterRequest.java
│   │   │       │   │   ├── CustomOrderRequest.java
│   │   │       │   │   └── ProductRequest.java
│   │   │       │   └── response/
│   │   │       │       ├── ApiResponse.java
│   │   │       │       ├── AuthResponse.java
│   │   │       │       ├── OrderResponse.java
│   │   │       │       └── ProductResponse.java
│   │   │       ├── entity/
│   │   │       │   ├── User.java
│   │   │       │   ├── Product.java
│   │   │       │   ├── CustomOrder.java
│   │   │       │   ├── OrderItem.java
│   │   │       │   ├── Delivery.java
│   │   │       │   ├── Category.java
│   │   │       │   └── RefreshToken.java
│   │   │       ├── enums/
│   │   │       │   ├── UserRole.java
│   │   │       │   ├── OrderStatus.java
│   │   │       │   ├── DeliveryStatus.java
│   │   │       │   └── ProductCategory.java
│   │   │       ├── exception/
│   │   │       │   ├── GlobalExceptionHandler.java
│   │   │       │   ├── ResourceNotFoundException.java
│   │   │       │   └── BadRequestException.java
│   │   │       ├── repository/
│   │   │       │   ├── UserRepository.java
│   │   │       │   ├── ProductRepository.java
│   │   │       │   ├── CustomOrderRepository.java
│   │   │       │   ├── OrderItemRepository.java
│   │   │       │   ├── DeliveryRespository.java
│   │   │       │   ├── CategoryRepository.java
│   │   │       │   └── RefreshTokenRepository.java
│   │   │       ├── security/
│   │   │       │   ├── JwtAuthenticationEntryPoint.java
│   │   │       │   ├── JwtAuthenticationFilter.java
│   │   │       │   ├── JwtTokenProvider.java
│   │   │       │   └── UserPrincipal.java
│   │   │       └── service/
│   │   │           ├── impl/
│   │   │           │   ├── AuthServiceImpl.java
│   │   │           │   ├── CustomerServiceImpl.java
│   │   │           │   ├── VendorServiceImpl.java
│   │   │           │   ├── DeliveryServiceImpl.java
│   │   │           │   ├── AdminServiceImpl.java
│   │   │           │   ├── FileStorageServiceImpl.java
│   │   │           │   ├── EmailServiceImpl.java
│   │   │           │   └── SmsServiceImpl.java
│   │   │           ├── AuthService.java
│   │   │           ├── CustomerService.java
│   │   │           ├── VendorService.java
│   │   │           ├── DeliveryService.java
│   │   │           ├── AdminService.java
│   │   │           ├── FileStorageService.java
│   │   │           ├── EmailService.java
│   │   │           └── SmsService.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── data.sql
│   └── test/
│       └── java/
│           └── com/customworld/
│               └── CustomWorldApplicationTests.java
├── pom.xml
├── README.md
└── LICENSE
```

### Description des dossiers
- **config/** : Configurations de l'application (sécurité, CORS, stockage de fichiers, Swagger).
- **controller/** : Endpoints REST pour l'authentification, les clients, vendeurs, livreurs, administrateurs et la gestion des fichiers.
- **dto/** : Objets de transfert de données (DTO) pour les requêtes et réponses API, séparés en `request` et `response`.
- **entity/** : Entités JPA représentant les tables de la base de données (User, Product, CustomOrder, RefreshToken, etc.).
- **enums/** : Énumérations pour les rôles, statuts de commande, statuts de livraison et catégories de produits.
- **exception/** : Gestion centralisée des exceptions avec des messages personnalisés.
- **repository/** : Interfaces Spring Data JPA pour interagir avec la base de données.
- **security/** : Composants pour l'authentification et l'autorisation via JWT.
- **service/** : Logique métier, avec interfaces et implémentations séparées pour chaque rôle, y compris les services d'email et SMS.
- **resources/** : Fichiers de configuration (`application.properties`) et données initiales (`data.sql`).

## Prérequis
- **Java 17** (JDK)
- **Maven 3.8+**
- **PostgreSQL 13+**
- Un IDE comme IntelliJ IDEA, Eclipse ou VS Code
- (Optionnel) Postman ou un navigateur pour tester l'API via Swagger
- (Optionnel) Compte Twilio pour les notifications SMS

## Installation
1. **Cloner le projet** :
   ```bash
   git clone https://github.com/Delmat237/CustomWorld.git
   cd customworld-backend
   ```

2. **Configurer la base de données** :
    - Créez une base de données PostgreSQL :
      ```sql
      CREATE DATABASE customworld;
      ```
    - Créez les tables nécessaires :
      ```sql
      CREATE TABLE users (
          id BIGSERIAL PRIMARY KEY,
          email VARCHAR(255) NOT NULL UNIQUE,
          password VARCHAR(255) NOT NULL,
          role VARCHAR(50) NOT NULL,
          name VARCHAR(255),
          address VARCHAR(255),
          phone VARCHAR(20),
          password_reset_token VARCHAR(255),
          password_reset_token_expiry TIMESTAMP
      );
 
      CREATE TABLE refresh_tokens (
          id BIGSERIAL PRIMARY KEY,
          user_id BIGINT NOT NULL REFERENCES users(id),
          token VARCHAR(255) NOT NULL UNIQUE,
          expiry_date TIMESTAMP NOT NULL
      );
      ```

3. **Construire le projet** :
   ```bash
   mvn clean install
   ```

4. **Lancer l'application** :
   ```bash
   mvn spring-boot:run
   ```
   L'application sera accessible à `http://localhost:8080`.

## Configuration
- **application.properties** :
    - **Base de données** : Configurez l'URL, l'utilisateur et le mot de passe PostgreSQL.
    - **JWT** : Remplacez `your_jwt_secret_key` par une clé sécurisée (au moins 32 caractères).
    - **Stockage des fichiers** : Le répertoire `./Uploads` est utilisé pour les fichiers uploadés. Assurez-vous qu'il existe ou sera créé.
    - **Email** : Configurez les paramètres SMTP (ex. Gmail) pour les notifications par email.
    - **SMS (optionnel)** : Configurez les identifiants Twilio pour les notifications SMS.
    - Exemple :
      ```properties
      spring.datasource.url=jdbc:postgresql://localhost:5432/customworld
      spring.datasource.username=springuser
      spring.datasource.password=****
      spring.jpa.hibernate.ddl-auto=update
      spring.jpa.show-sql=true
      spring.servlet.multipart.max-file-size=10MB
      spring.servlet.multipart.max-request-size=10MB
      jwt.secret=your_jwt_secret_key
      jwt.expiration=86400000
      jwt.refresh-expiration=604800000
      jwt.reset-token-expiration=1800000
      file.upload-dir=./Uploads
      spring.mail.host=smtp.gmail.com
      spring.mail.port=587
      spring.mail.username=votre.email@gmail.com
      spring.mail.password=votre-mot-de-passe-d-application
      spring.mail.properties.mail.smtp.auth=true
      spring.mail.properties.mail.smtp.starttls.enable=true
      twilio.account.sid=your_twilio_account_sid
      twilio.auth.token=your_twilio_auth_token
      twilio.phone.number=+1234567890
      ```

- **Données initiales** :
    - Le fichier `data.sql` initialise des catégories et des utilisateurs de test (mot de passe par défaut : `password`, haché avec BCrypt).

## Utilisation
1. **Accéder à l'API** :
    - L'API est disponible à `http://localhost:8080/api`.
    - Consultez la documentation Swagger à `http://localhost:8080/swagger-ui/index.html`.

2. **Endpoints principaux** :
    - **Authentification** :
        - `POST /api/auth/login` : Connexion (retourne un access token et un refresh token).
        - `POST /api/auth/register` : Inscription d'un nouvel utilisateur (envoie un email et, optionnellement, un SMS de bienvenue).
        - `POST /api/auth/refresh-token` : Rafraîchir l'access token avec un refresh token.
        - `POST /api/auth/reset-password-request` : Demander une réinitialisation de mot de passe (envoie un email avec un lien).
        - `POST /api/auth/reset-password` : Réinitialiser le mot de passe avec un token.
    - **Client** :
        - `GET /api/customer/products` : Lister les produits.
        - `POST /api/customer/orders` : Passer une commande.
        - `POST /api/customer/orders/upload` : Uploader une image.
        - `GET /api/customer/orders?customerId={id}` : Consulter les commandes.
    - **Vendeur** :
        - `POST /api/vendor/products` : Publier un produit.
        - `POST /api/vendor/products/image` : Uploader une image de produit.
        - `GET /api/vendor/products?vendorId={id}` : Lister les produits du vendeur.
    - **Livreur** :
        - `GET /api/delivery/deliveries?delivererId={id}` : Lister les livraisons assignées.
        - `PUT /api/delivery/deliveries/{id}/status?status={status}` : Mettre à jour le statut d'une livraison.
    - **Administrateur** :
        - `GET /api/admin/users` : Lister tous les utilisateurs.
        - `POST /api/admin/users` : Créer un utilisateur.
        - `GET /api/admin/orders` : Lister toutes les commandes.
        - `PUT /api/admin/orders/{id}/assign?delivererId={id}` : Assigner une commande à un livreur.
        - `PUT /api/admin/products/{id}/validate` : Valider un produit.

3. **Authentification** :
    - Obtenez un token JWT via `/api/auth/login`.
    - Incluez le token dans l'en-tête des requêtes : `Authorization: Bearer <accessToken>`.

4. **Upload de fichiers** :
    - Utilisez `/api/files/upload` pour uploader des images (max 10MB).
    - Récupérez les fichiers via `/api/files/{fileName}`.

5. **Notifications** :
    - Les emails sont envoyés pour l'inscription et la réinitialisation de mot de passe.
    - Les SMS (optionnels) sont envoyés lors de l'inscription si un numéro de téléphone est fourni.

## Documentation de l'API
- L'API est documentée avec **Swagger**. Accédez-y via :
  ```
  http://localhost:8080/swagger-ui/index.html
  ```
- La documentation inclut tous les endpoints, paramètres, et exemples de requêtes/réponses.

## Tests
- Un test unitaire de base est inclus dans `src/test/java/com/customworld/CustomWorldApplicationTests.java`.
- Pour exécuter les tests :
  ```bash
  mvn test
  ```
- Exemple de test pour l'inscription :
  ```bash
  curl -X POST "http://localhost:8080/api/auth/register" -H "Content-Type: application/json" -d '{
      "name": "Test User",
      "email": "test@example.com",
      "password": "password123",
      "address": "123 Rue Exemple",
      "role": "CLIENT",
      "phone": "+1234567890"
  }'
  ```
- Vérifiez les logs pour les erreurs d'envoi d'email ou de SMS (non bloquants).

## Déploiement
1. **Local** :
    - Suivez les étapes d'installation ci-dessus.
    - Assurez-vous que PostgreSQL est en cours d'exécution.

2. **Production** :
    - Utilisez un service cloud comme AWS S3 pour le stockage des fichiers à la place du stockage local.
    - Configurez un service SMTP (ex. AWS SES, SendGrid) pour les emails.
    - Configurez Twilio pour les SMS (optionnel).
    - Déployez sur une plateforme comme Heroku, AWS Elastic Beanstalk, ou un conteneur Docker avec une base de données PostgreSQL managée.

## Contribuer
1. Forkez le projet.
2. Créez une branche pour vos modifications (`git checkout -b feature/nouvelle-fonctionnalite`).
3. Commitez vos modifications (`git commit -am 'Ajout de nouvelle fonctionnalité'`).
4. Poussez votre branche (`git push origin feature/nouvelle-fonctionnalite`).
5. Créez un pull request.

## Contact
- **Nom** : Leonel Delmat
- **Email** : azangueleonel9@gmail.com
- **WhatsApp** : +237 657 450 314
- **GitHub** : [Delmat237](https://github.com/Delmat237)
- **LinkedIn** : [Leonel Azangue](https://www.linkedin.com/in/leonel-azangue)

## Licence
Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.