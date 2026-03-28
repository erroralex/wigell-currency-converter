# Wigell Currency Converter

En lättviktig mikrotjänst byggd med **Java 24** och **Spring Boot 3.5.4** för att hantera valutakonvertering inom Wigell-ekosystemet. Tjänsten hämtar aktuella växelkurser i realtid via Frankfurter API.

## API-Dokumentation
Tjänsten exponerar en endpoint för att hämta växelkursen mellan två valutor.

### Hämta växelkurs
* **Metod:** `GET`
* **URL:** `/api/v1/currency/convert`
* **Parametrar:**
    * `base`: Ursprungsvaluta (t.ex. `SEK`)
    * `target`: Målvaluta (t.ex. `EUR`)
* **Svar:** En `Double` som representerar växelkursen för 1 enhet av ursprungsvalutan.

---

## Integrationsguide (Concise)

Denna guide är till för andra tjänster (t.ex. `wigell-paddel`) som behöver utföra valutakonvertering.

### 1. URL-Konfiguration
Anslut till tjänsten via gatewayen (port 8080) eller direkt på port 8580.

**Exempel i application.yml:**
```yaml
wigell:
  currency:
    url: http://localhost:8080/currency # Via Gateway
```

### 2. Anrop från kod
Använd `CurrencyClient` från vårt `shared-lib` för att göra anropet. Tjänsten returnerar ett rent siffervärde (`Double`), vilket gör det enkelt att multiplicera direkt med era priser.

**Exempel i en Service-klass:**
```java
// Hämta kursen (t.ex. 0.088 för SEK -> EUR)
double exchangeRate = currencyClient.getExchangeRate("SEK", "EUR");

// Beräkna totalpris
BigDecimal totalPriceEur = priceInSek.multiply(BigDecimal.valueOf(exchangeRate));
```

### 3. Hantering av svar
* Vid framgång: Status `200 OK` och växelkursen som en siffra i bodyn.
* Vid fel:
    * `400 Bad Request`: Ogiltiga valutakoder.
    * `503 Service Unavailable`: Om den externa källan (Frankfurter) ligger nere.

---
*Utvecklad av Grupp C*
