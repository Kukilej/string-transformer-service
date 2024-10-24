# String Transformer Service

## Overview
String Transformer Service is a Spring Boot-based application that applies a series of transformations to strings. It supports various types of transformations, including regex-based replacements and script conversions (e.g., Cyrillic/Greek to Latin).

## Features
- Accepts a collection of elements, each with a string value and a list of transformers.
- Applies transformations in the specified order and returns both the original and transformed values.
- Provides built-in validation for inputs, including null checks and parameter validation.
- Supports extensible transformer architecture through annotations.

## Prerequisites
- Java 17 or higher
- Maven 3.8 or higher

## Getting Started

### Running the Application
1. Clone the repository:
2. Navigate to the project directory
3. Build with  mvn clean install
4. Run with  mvn spring-boot:run


## API Endpoints
### Transform Strings: POST /api/v1/transform

### Request Body:
```json
{
   "elements": [
      {
         "value": "Sample123Text",
         "transformers": [
            {
               "groupId": "regex",
               "transformerId": "remove",
               "parameters": {
                  "pattern": "\\d+"
               }
            },
            {
               "groupId": "regex",
               "transformerId": "replace",
               "parameters": {
                  "pattern": "Text",
                  "replacement": "String"
               }
            },
            {
               "groupId": "script",
               "transformerId": "convert",
               "parameters": {}
            }
         ]
      },
      {
         "value": "Пример456Текстα",
         "transformers": [
            {
               "groupId": "regex",
               "transformerId": "remove",
               "parameters": {
                  "pattern": "\\d+"
               }
            },
            {
               "groupId": "script",
               "transformerId": "convert",
               "parameters": {}
            }
         ]
      }
   ]
}

```
### Response:

```json
[
	{
		"originalValue": "Sample123Text",
		"transformedValue": "SampleString"
	},
	{
		"originalValue": "Пример456Текстα",
		"transformedValue": "PrimerTeksta"
	}
]
```