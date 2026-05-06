# Mesh-Core
Mesh-Core is a high-performance, non-blocking Financial Service Mesh built with Java 21 and Spring Cloud Gateway. It acts as the intelligent edge for the ByteEntropy ecosystem, providing security, compliance, and protocol mediation.

## 🚀 Key Features
### * Java 21 Virtual Threads (Loom): 
Optimized for high-concurrency without thread-pinning, allowing the gateway to handle 50k+ TPS on lean infrastructure.
### * Zero-Trust Security: 
Enforced mTLS (Mutual TLS) at the transport layer and HMAC-SHA256 signature verification at the application layer.
### * PCI-DSS Compliance Shield: 
An edge-tokenization filter that intercepts and swaps sensitive PAN data for tokens before traffic enters the internal network.
### * Protocol Mediation: 
Dynamically transforms modern JSON payloads into banking-standard ISO-20022 XML messages.
### * Resilience: 
Integrated Resilience4j circuit breakers to prevent cascading failures across the fintech core.

## 🏗 Architecture Flow
### * Ingress: 
Client initiates mTLS handshake (Zero-Trust).

### * Auth: 
HmacFilter validates request integrity via X-Signature.

### * Compliance: 
TokenizationFilter swaps sensitive data for internal tokens.

### * Standardization: 
IsoMediationFilter performs protocol transformation.
### * Dispatch: 
Request is routed via Virtual Threads to the target micro-core.

## 🔐 Security Setup (mTLS)
Before running the server, you must generate the internal PKI chain.

### 1. Generate Development Certificates

Run these commands in src/main/resources/certs/ to create a local Certificate Authority and signed keys:

```
Bash

# Generate CA
openssl req -x509 -sha256 -days 365 -newkey rsa:2048 -keyout ca.key -out ca.crt -nodes -subj "/CN=ByteEntropyCA"

# Generate Server Identity (Keystore)
keytool -genkeypair -alias mesh-server -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore mesh-server.p12 -validity 365 -storepass password -dname "CN=localhost, OU=Engineering, O=ByteEntropy"

# Generate Truststore (Imports CA to trust clients signed by it)
keytool -importcert -alias mesh-ca -file ca.crt -keystore mesh-truststore.p12 -storepass password -noprompt

# Generate Client Cert for testing (CURL)
openssl req -newkey rsa:2048 -keyout mesh.key -out mesh.csr -nodes -subj "/CN=client"
openssl x509 -req -days 365 -in mesh.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out mesh.crt
```

### 2. Export Environment Secrets
The application retrieves keystore passwords from the environment to prevent credential leakage in source code:

```
Bash
export MESH_KEYSTORE_PASS=password
export MESH_TRUSTSTORE_PASS=password
```

## 🚦 Running the Mesh

### Build

```
Bash
mvn clean install -DskipTests
```

### Run

```
Bash
mvn spring-boot:run
```

## 🧪 Verification Suite
To verify the Zero-Trust perimeter, use curl with the generated client certificates.

### Test 1: Valid mTLS + Valid HMAC (Success Path)
A successful request will pass security and return a 503 or 404 (indicating the backend service is offline, but the gateway is open).

```
Bash
curl -v -k 
  --cert src/main/resources/certs/mesh.crt 
  --key src/main/resources/certs/mesh.key 
  -H "X-Signature: valid_mock_signature" 
  https://localhost:8443/trade/status
```

### Test 2: Missing HMAC (Security Block)

```Bash
curl -v -k 
  --cert src/main/resources/certs/mesh.crt 
  --key src/main/resources/certs/mesh.key 
  https://localhost:8443/trade/status
# Result: 401 Unauthorized
```

### Test 3: Missing Certificate (Transport Block)

```
Bash
curl -v -k https://localhost:8443/trade/status
# Result: SSL Alert Bad Certificate (Connection Closed)
```

## 🛠 Tech Stack
* Runtime: Java 21 (Loom / Virtual Threads)
* Framework: Spring Boot 3.4.0 / Spring Cloud Gateway
* Security: Spring Security (X.509/mTLS) + HMAC
* Resilience: Resilience4j
* NObservability: Micrometer + Prometheus

## 📄 License
MIT License - Copyright (c) 2026 ByteEntropy