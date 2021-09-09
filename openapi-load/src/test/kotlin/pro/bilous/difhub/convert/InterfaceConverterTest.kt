package pro.bilous.difhub.convert

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.jupiter.api.Test
import io.swagger.util.Json
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.media.*
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.parameters.PathParameter
import io.swagger.v3.oas.models.parameters.QueryParameter
import io.swagger.v3.oas.models.parameters.RequestBody
import pro.bilous.difhub.model.Model
import kotlin.test.assertEquals

class InterfaceConverterTest {

	companion object {
		private const val ref = "\$ref"
		val INTERFACE = """
{
    "identity": {
        "id": "662a25e2-0a4e-4fe3-84da-2f8da3826eb5",
        "name": "Products",
        "description": "Interface to access product information"
    },
    "version": {
        "major": 1,
        "minor": 0,
        "revision": 0
    },
    "object": {
        "parent": {
            "id": "beb5cbfa-548e-4b5b-ad8a-e9d168062482",
            "name": "/organizations/DJet/systems/Client/applications/Product"
        },
        "alias": "",
        "picture": "",
        "tags": [],
        "documents": [],
        "type": "Interface",
        "usage": "RESTful API",
        "access": "Public",
        "contact": {
            "identity": {
                "id": "",
                "name": "",
                "description": "",
                "translations": []
            },
            "URL": "",
            "EMail": ""
        },
        "properties": [],
        "elements": [],
        "history": {
            "created": "2021-05-07T07:07:20.927",
            "createdBy": "v.nahornyi@spd-ukraine.com",
            "updated": "2021-06-07T12:57:06.013",
            "updatedBy": "v.nahornyi@spd-ukraine.com",
            "completions": []
        },
        "publicationCount": 0,
        "subscriptionCount": 0
    },
    "subscription": {},
    "servers": [],
    "path": "/​productlines/​{productLineId}/products/{productId}",
    "parameters": [
        {
            "location": "Body",
            "field": {
                "identity": {
                    "id": "b1bc2455-cd78-41ee-82a6-a83ede8d1601",
                    "name": "product",
                    "description": "Product data specification."
                },
                "order": 3,
                "usage": "Property",
                "count": 1,
                "type": "Structure",
                "reference": "/organizations/DJet/systems/Client/applications/Product/datasets/Product/versions/1.0.0",
                "optional": false,
                "value": "",
                "format": "",
                "access": 255,
                "privacy": "low"
            }
        },
        {
            "location": "Path",
            "field": {
                "identity": {
                    "id": "7beb70cd-a308-47d5-a991-095e07cd8087",
                    "name": "productId",
                    "description": "Product Id to fetch and put records"
                },
                "order": 2,
                "usage": "Property",
                "count": 1,
                "type": "String",
                "size": 0,
                "optional": true,
                "value": "",
                "format": "",
                "access": 255,
                "privacy": "low"
            }
        },
        {
            "location": "Path",
            "field": {
                "identity": {
                    "id": "f441099c-419f-4e09-a06d-b9ed7f331fed",
                    "name": "productLineId",
                    "description": "Id of Product Line"
                },
                "order": 4,
                "usage": "Property",
                "count": 1,
                "type": "String",
                "size": 0,
                "optional": true,
                "value": "",
                "format": "",
                "access": 255,
                "privacy": "low"
            }
        },
        {
            "location": "Query",
            "field": {
                "identity": {
                    "id": "d46a8f1c-4be8-4997-871a-f20059e7364b",
                    "name": "search",
                    "description": "Expression to limit number of entities in response."
                },
                "order": 1,
                "usage": "Property",
                "count": 1,
                "type": "String",
                "size": 0,
                "optional": true,
                "value": "",
                "format": "",
                "access": 255,
                "privacy": "low"
            }
        },
        {
            "location": "Header",
            "field": {
                "identity": {
                    "id": "addadecd-9ec0-46f4-820a-f95a7f802387",
                    "name": "partyId",
                    "description": "for filtering as a sample of Guid"
                },
                "order": 5,
                "usage": "Property",
                "count": 1,
                "type": "String",
                "size": 36,
                "optional": true,
                "value": "",
                "format": "",
                "access": 255,
                "privacy": "low"
            }
        },
        {
            "location": "Header",
            "field": {
                "identity": {
                    "id": "e670f707-3222-4096-9419-c865a2990139",
                    "name": "critical",
                    "description": "for filtering as a sample of boolean"
                },
                "order": 6,
                "usage": "Property",
                "count": 1,
                "type": "Boolean",
                "optional": true,
                "value": "",
                "format": "",
                "access": 255,
                "privacy": "low"
            }
        },
        {
            "location": "Header",
            "field": {
                "identity": {
                    "id": "4d5ddc1b-0245-4f7f-b2e8-c41ae79c13a2",
                    "name": "type",
                    "description": "for filtering as a sample of Enum"
                },
                "order": 7,
                "usage": "Property",
                "count": 1,
                "type": "String",
                "optional": true,
                "value": "",
                "format": "",
                "access": 255,
                "privacy": "low"
            }
        },
        {
            "location": "Header",
            "field": {
                "identity": {
                    "id": "b7738667-c6b3-44e2-954a-68074837c2c9",
                    "name": "rank",
                    "description": "for filtering as a sample of Integer"
                },
                "order": 8,
                "usage": "Property",
                "count": 1,
                "type": "Integer",
                "optional": true,
                "value": "",
                "format": "",
                "access": 255,
                "privacy": "low"
            }
        },
        {
            "location": "Header",
            "field": {
                "identity": {
                    "id": "20818191-d951-4c07-8b8c-46c452bc260f",
                    "name": "estimation",
                    "description": "for filtering as a sample of String"
                },
                "order": 9,
                "usage": "Property",
                "count": 1,
                "type": "String",
                "optional": true,
                "value": "",
                "format": "",
                "access": 255,
                "privacy": "low"
            }
        }
    ],
    "responses": [
        {
            "code": "200",
            "field": {
                "identity": {
                    "id": "c2d2b797-34a8-4767-8bba-804e576e778b",
                    "name": "Success",
                    "description": "Contact is found and returned."
                },
                "order": 1,
                "usage": "Property",
                "count": 1,
                "type": "Structure",
                "reference": "/organizations/DJet/systems/Client/applications/Product/datasets/Product/versions/1.0.0",
                "optional": true,
                "keys": false,
                "privacy": "low"
            }
        },
        {
            "code": "201",
            "field": {
                "identity": {
                    "id": "42d9d810-a643-42af-ac14-a2c90e71f0d7",
                    "name": "SuccessCreated",
                    "description": "Object created successfully."
                },
                "order": 2,
                "usage": "Property",
                "count": 1,
                "type": "String",
                "size": 0,
                "optional": true,
                "value": "",
                "format": "",
                "access": 255,
                "privacy": "low"
            }
        },
        {
            "code": "204",
            "field": {
                "identity": {
                    "id": "ca9b161d-f830-45f2-8e97-ded162760752",
                    "name": "SuccessCompleted",
                    "description": "Operation completed successfully."
                },
                "order": 3,
                "usage": "Property",
                "count": 1,
                "type": "String",
                "size": 0,
                "optional": true,
                "value": "",
                "format": "",
                "access": 255,
                "privacy": "low"
            }
        },
        {
            "code": "400",
            "field": {
                "identity": {
                    "id": "0df804f6-25c3-4614-85bd-2e05fe8f224c",
                    "name": "Failure",
                    "description": "Execution of user request failed."
                },
                "order": 4,
                "usage": "Property",
                "count": 0,
                "type": "Structure",
                "reference": "/organizations/Infort Technologies/datasets/Error/versions/1.1.0",
                "optional": true,
                "value": "",
                "format": "",
                "access": 0,
                "keys": false,
                "privacy": "low"
            }
        },
        {
            "code": "401",
            "field": {
                "identity": {
                    "id": "b54c4e65-8710-4885-a909-9af30b76a767",
                    "name": "FailureAlreadyExist",
                    "description": "Contact is already exist."
                },
                "order": 5,
                "usage": "Property",
                "count": 1,
                "type": "Structure",
                "reference": "/organizations/Infort Technologies/datasets/Error/versions/1.1.0",
                "optional": true,
                "keys": false,
                "privacy": "low"
            }
        },
        {
            "code": "404",
            "field": {
                "identity": {
                    "id": "b2f61bd0-cc2b-42c9-9bc1-27b15b569499",
                    "name": "FailureNotFound",
                    "description": "Contact is not found"
                },
                "order": 6,
                "usage": "Property",
                "count": 1,
                "type": "Structure",
                "reference": "/organizations/Infort Technologies/datasets/Error/versions/1.1.0",
                "optional": true,
                "keys": false,
                "privacy": "low"
            }
        }
    ],
    "operations": [
        {
            "identity": {
                "id": "c9126b32-3e77-4c3f-98de-652063f5c6bb",
                "name": "CreateProduct",
                "description": "Create a new Product. Validate Product not yet exist."
            },
            "deprecated": false,
            "action": "POST",
            "parameters": [
                {
                    "id": "41fe449e-b8d4-4b7a-8611-c4053fc1a7e3",
                    "name": "product",
                    "description": "Product data specification.\n"
                },
                {
                    "id": "0b0f57ed-161a-4cb2-a036-36ac4d90b4c5",
                    "name": "productLineId",
                    "description": "Id of Product Line"
                }
            ],
            "responses": [
                {
                    "id": "79c59e2e-c8e5-406b-9e40-32d50e16ba64",
                    "name": "SuccessCreated",
                    "description": "Object created successfully."
                },
                {
                    "id": "00f1bcf5-a204-43bb-afdf-1e7e1e07102b",
                    "name": "Failure",
                    "description": "Execution of user request failed."
                },
                {
                    "id": "6385e469-fac5-49f4-8eb8-b9e6fe902b9d",
                    "name": "FailureAlreadyExist",
                    "description": "Contact is already exist."
                }
            ]
        },
        {
            "identity": {
                "id": "64c55b57-f0c4-486b-ac92-7d945b5c673c",
                "name": "DeleteProduct",
                "description": "Soft delete of the product by id."
            },
            "deprecated": false,
            "action": "DELETE",
            "parameters": [
                {
                    "id": "04244816-4e4a-4340-9c56-62233cb796ce",
                    "name": "productId",
                    "description": "Product Id to fetch and put records"
                },
                {
                    "id": "0b0f57ed-161a-4cb2-a036-36ac4d90b4c5",
                    "name": "productLineId",
                    "description": "Id of Product Line"
                }
            ],
            "responses": [
                {
                    "id": "a5dfede5-b294-44cd-9a8a-0a7c80a40e69",
                    "name": "SuccessCompleted",
                    "description": "Operation completed successfully."
                },
                {
                    "id": "00f1bcf5-a204-43bb-afdf-1e7e1e07102b",
                    "name": "Failure",
                    "description": "Execution of user request failed."
                },
                {
                    "id": "72af19d9-e3d6-4bb1-b6e8-d719363f597a",
                    "name": "FailureNotFound",
                    "description": "Contact is not found"
                }
            ]
        },
        {
            "identity": {
                "id": "62819c2f-0f23-4bb4-9bfa-f0e56ea5c873",
                "name": "GetProduct",
                "description": "Get a product or list of products from the service."
            },
            "deprecated": false,
            "action": "GET",
            "parameters": [
                {
                    "id": "4a98c672-73dc-40a2-93b0-b70365cfb9b3",
                    "name": "search",
                    "description": "Expression to limit number of entities in response."
                },
                {
                    "id": "04244816-4e4a-4340-9c56-62233cb796ce",
                    "name": "productId",
                    "description": "Product Id to fetch and put records"
                },
                {
                    "id": "0b0f57ed-161a-4cb2-a036-36ac4d90b4c5",
                    "name": "productLineId",
                    "description": "Id of Product Line"
                },
                {
                    "id": "addadecd-9ec0-46f4-820a-f95a7f802387",
                    "name": "partyId",
                    "description": "for filtering"
                },
                {
                    "id": "4d5ddc1b-0245-4f7f-b2e8-c41ae79c13a2",
                    "name": "type",
                    "description": "for filtering as a sample of Enum"
                },
                {
                    "id": "e670f707-3222-4096-9419-c865a2990139",
                    "name": "critical",
                    "description": "for filtering as a sample of boolean"
                },
                {
                    "id": "b7738667-c6b3-44e2-954a-68074837c2c9",
                    "name": "rank",
                    "description": "for filtering as a sample of Integer"
                },
                {
                    "id": "20818191-d951-4c07-8b8c-46c452bc260f",
                    "name": "estimation",
                    "description": "for filtering as a sample of String"
                }
            ],
            "responses": [
                {
                    "id": "3c0cb368-40de-4019-ad04-69b9300f95b8",
                    "name": "Success",
                    "description": "Contact is found and returned."
                },
                {
                    "id": "00f1bcf5-a204-43bb-afdf-1e7e1e07102b",
                    "name": "Failure",
                    "description": "Execution of user request failed."
                },
                {
                    "id": "72af19d9-e3d6-4bb1-b6e8-d719363f597a",
                    "name": "FailureNotFound",
                    "description": "Contact is not found"
                }
            ]
        },
        {
            "identity": {
                "id": "9bb89e21-fe1b-42b4-bb3e-3ba06b461330",
                "name": "ModifyProduct",
                "description": "Change Product properties to values specified in the request. Property not specified will remain the same. No element of any collection will be deleted."
            },
            "deprecated": false,
            "action": "PATCH",
            "parameters": [
                {
                    "id": "04244816-4e4a-4340-9c56-62233cb796ce",
                    "name": "productId",
                    "description": "Product Id to fetch and put records"
                },
                {
                    "id": "41fe449e-b8d4-4b7a-8611-c4053fc1a7e3",
                    "name": "product",
                    "description": "Product data specification.\n"
                },
                {
                    "id": "0b0f57ed-161a-4cb2-a036-36ac4d90b4c5",
                    "name": "productLineId",
                    "description": "Id of Product Line"
                },
                {
                    "id": "addadecd-9ec0-46f4-820a-f95a7f802387",
                    "name": "partyId",
                    "description": "for filtering as a sample of Guid"
                },
                {
                    "id": "b7738667-c6b3-44e2-954a-68074837c2c9",
                    "name": "rank",
                    "description": "for filtering as a sample of Integer"
                }
            ],
            "responses": [
                {
                    "id": "a5dfede5-b294-44cd-9a8a-0a7c80a40e69",
                    "name": "SuccessCompleted",
                    "description": "Operation completed successfully."
                },
                {
                    "id": "00f1bcf5-a204-43bb-afdf-1e7e1e07102b",
                    "name": "Failure",
                    "description": "Execution of user request failed."
                },
                {
                    "id": "72af19d9-e3d6-4bb1-b6e8-d719363f597a",
                    "name": "FailureNotFound",
                    "description": "Contact is not found"
                }
            ]
        },
        {
            "identity": {
                "id": "926f8937-5b56-4716-83ba-4c25fa10cbd6",
                "name": "UpdateProduct",
                "description": "Change the product specification."
            },
            "deprecated": false,
            "action": "PUT",
            "parameters": [
                {
                    "id": "04244816-4e4a-4340-9c56-62233cb796ce",
                    "name": "productId",
                    "description": "Product Id to fetch and put records"
                },
                {
                    "id": "41fe449e-b8d4-4b7a-8611-c4053fc1a7e3",
                    "name": "product",
                    "description": "Product data specification.\n"
                },
                {
                    "id": "0b0f57ed-161a-4cb2-a036-36ac4d90b4c5",
                    "name": "productLineId",
                    "description": "Id of Product Line"
                },
                {
                    "id": "addadecd-9ec0-46f4-820a-f95a7f802387",
                    "name": "partyId",
                    "description": "for filtering as a sample of Guid"
                }
            ],
            "responses": [
                {
                    "id": "a5dfede5-b294-44cd-9a8a-0a7c80a40e69",
                    "name": "SuccessCompleted",
                    "description": "Operation completed successfully."
                },
                {
                    "id": "00f1bcf5-a204-43bb-afdf-1e7e1e07102b",
                    "name": "Failure",
                    "description": "Execution of user request failed."
                },
                {
                    "id": "72af19d9-e3d6-4bb1-b6e8-d719363f597a",
                    "name": "FailureNotFound",
                    "description": "Contact is not found"
                }
            ]
        }
    ],
    "layouts": [],
    "examples": []
}
		""".trimIndent()

		val PRODUCT_PATH = """
			{
			    "get": {
			        "tags": ["Products"],
			        "summary": "GetProduct",
			        "description": "Get a product or list of products from the service.",
			        "operationId": "productsGetProduct",
			        "parameters": [],
			        "responses": {
			            "200": {
			                "description": "Contact is found and returned.",
			                "content": {
			                    "application/json": {
			                        "schema": {
			                            "$ref": "#/components/schemas/Product"
			                        }
			                    }
			                }
			            },
			            "400": {
			                "description": "Execution of user request failed.",
			                "content": {
			                    "application/json": {
			                        "schema": {
			                            "type": "array",
										"$ref": "#/components/schemas/Error"
			                        }
			                    }
			                }
			            },
			            "404": {
			                "description": "Contact is not found",
			                "content": {
			                    "application/json": {
			                        "schema": {
			                            "$ref": "#/components/schemas/Error"
			                        }
			                    }
			                }
			            }
			        }
			    },
			    "put": {
			        "tags": ["Products"],
			        "summary": "UpdateProduct",
			        "description": "Change the product specification.",
			        "operationId": "productsUpdateProduct",
			        "requestBody": {
			            "$ref": "#/components/requestBodies/product"
			        },
			        "responses": {
			            "204": {
			                "description": "Operation completed successfully."
			            },
			            "400": {
			                "description": "Execution of user request failed.",
			                "content": {
			                    "application/json": {
			                        "schema": {
										"type": "array",
									    "$ref": "#/components/schemas/Error"
			                        }
			                    }
			                }
			            },
			            "404": {
			                "description": "Contact is not found",
			                "content": {
			                    "application/json": {
			                        "schema": {
			                            "$ref": "#/components/schemas/Error"
			                        }
			                    }
			                }
			            }
			        }
			    },
			    "delete": {
			        "tags": ["Products"],
			        "summary": "DeleteProduct",
			        "description": "Soft delete of the product by id.",
			        "operationId": "productsDeleteProduct",
			        "responses": {
			            "204": {
			                "description": "Operation completed successfully."
			            },
			            "400": {
			                "description": "Execution of user request failed.",
			                "content": {
			                    "application/json": {
			                        "schema": {
			                            "type": "array",
		                                "$ref": "#/components/schemas/Error"
			                        }
			                    }
			                }
			            },
			            "404": {
			                "description": "Contact is not found",
			                "content": {
			                    "application/json": {
			                        "schema": {
			                            "$ref": "#/components/schemas/Error"
			                        }
			                    }
			                }
			            }
			        }
			    },
			    "patch": {
			        "tags": ["Products"],
			        "summary": "ModifyProduct",
			        "description": "Change Product properties to values specified in the request. Property not specified will remain the same. No element of any collection will be deleted.",
			        "operationId": "productsModifyProduct",
			        "requestBody": {
			            "$ref": "#/components/requestBodies/product"
			        },
			        "responses": {
			            "204": {
			                "description": "Operation completed successfully."
			            },
			            "400": {
			                "description": "Execution of user request failed.",
			                "content": {
			                    "application/json": {
			                        "schema": {
			                            "type": "array",
		                                "$ref": "#/components/schemas/Error"
			                        }
			                    }
			                }
			            },
			            "404": {
			                "description": "Contact is not found",
			                "content": {
			                    "application/json": {
			                        "schema": {
			                            "$ref": "#/components/schemas/Error"
			                        }
			                    }
			                }
			            }
			        }
			    },
			    "parameters": [
			        {
			             "$ref": "#/components/parameters/productLineId"
			        },
			        {
			             "$ref": "#/components/parameters/productId"
			        }
			    ]
			}
		""".trimIndent()

		const val TEST_PRODUCT_MODEL =
			"/organizations/DJet/systems/Client/applications/Product/datasets/Product/versions/1.0.0"
		const val TEST_ERROR_MODEL = "/organizations/Infort Technologies/datasets/Error/versions/1.1.0"

		val PARAMETER_PRODUCT_ID = """
			{
			    "name": "productId",
			    "description": "Product Id to fetch and put records",
			    "schema": {
			         "type": "string"
			    },
			    "in": "path",
			    "required": "true"
			}
		""".trimIndent()

		val PARAMETER_PRODUCT_LINE_ID = """
			{
			    "name": "productLineId",
			    "description": "Id of Product Line",
			    "schema": {
			         "type": "string"
			    },
			    "in": "path",
			    "required": "true"
			}
		""".trimIndent()

		val PARAMETER_SEARCH = """
			{
			    "name": "search",
			    "description": "Expression to limit number of entities in response.",
			    "schema": {
			         "type": "string"
			    },
			    "in": "query",
				"required": "false"
			}
		""".trimIndent()

		val BODY = """
			{
			    "description": "Product data specification.",
			    "content": {
			        "application/json": {
			            "schema": {
			                "$ref": "#/components/schemas/Product"
			            }
			        }
			    },
			    "required": "true"
			}
		""".trimIndent()

		val mapper: ObjectMapper = Json.mapper().apply {
			registerKotlinModule()
		}
	}

	private inline fun <reified T> ObjectMapper.readValue(json: String): T =
		readValue(json, object : TypeReference<T>() {})

	@Test
	fun `should return paths`() {
		val interfaceModel = mapper.readValue<Model>(INTERFACE)
		val converter = InterfaceConverter(interfaceModel).apply {
			convert()
		}
		val paths = converter.paths
		val productPath = mapper.readValue<PathItem>(PRODUCT_PATH).apply {
			resolveArraySchemasForPath(this)
		}
		assertEquals(productPath, paths["/productlines/{productLineId}/products/{productId}"])
	}

	private fun resolveArraySchemasForPath(path: PathItem) {
		resolveArraySchemasForOperation(path.get)
		resolveArraySchemasForOperation(path.post)
		resolveArraySchemasForOperation(path.put)
		resolveArraySchemasForOperation(path.delete)
		resolveArraySchemasForOperation(path.patch)
		resolveArraySchemasForOperation(path.options)
		resolveArraySchemasForOperation(path.head)
		resolveArraySchemasForOperation(path.trace)
	}

	private fun resolveArraySchemasForOperation(operation: Operation?) {
		operation?.responses?.values?.forEach { response ->
			response?.content?.forEach {
				resolveArraySchemaForMediaType(it.value)
			}
		}
	}

	private fun resolveArraySchemaForMediaType(mediaType: MediaType?) {
		mediaType?.schema?.run {
			if (type != null && type == "array" && `$ref` != null) {
				val typeSchema = Schema<Any>().`$ref`(`$ref`)
				val arraySchema = ArraySchema().items(typeSchema)
				mediaType.schema = arraySchema
			}
		}
	}

	@Test
	fun `should return pathModels`() {
		val interfaceModel = mapper.readValue<Model>(INTERFACE)
		val converter = InterfaceConverter(interfaceModel).apply {
			convert()
		}
		val pathModels = converter.pathModels
		assertEquals(TEST_PRODUCT_MODEL, pathModels["Product"])
		assertEquals(TEST_ERROR_MODEL, pathModels["Error"])
	}

	@Test
	fun `should return parameters`() {
		val interfaceModel = mapper.readValue<Model>(INTERFACE)
		val converter = InterfaceConverter(interfaceModel).apply {
			convert()
		}
		val parameters = converter.parameters

		val paramProductId = mapper.readValue<PathParameter>(PARAMETER_PRODUCT_ID).apply {
			resolveSchemaForPathParameter(this)
		}
		assertEquals(paramProductId, parameters["productId"])

		val paramProductLineId = mapper.readValue<PathParameter>(PARAMETER_PRODUCT_LINE_ID).apply {
			resolveSchemaForPathParameter(this)
		}
		assertEquals(paramProductLineId, parameters["productLineId"])

		val paramSearch = mapper.readValue<QueryParameter>(PARAMETER_SEARCH).apply {
			resolveSchemaForPathParameter(this)
		}
		assertEquals(paramSearch, parameters["search"])
	}

	private fun resolveSchemaForPathParameter(parameter: Parameter?) {
		parameter?.schema?.run {
			parameter.schema = when (type) {
				"string" -> StringSchema()
				else -> ObjectSchema()
			}
		}
	}

	@Test
	fun `should return bodies`() {
		val interfaceModel = mapper.readValue<Model>(INTERFACE)
		val converter = InterfaceConverter(interfaceModel).apply {
			convert()
		}
		val requestBodies = converter.requestBodies
		val testBody = mapper.readValue<RequestBody>(BODY)
		assertEquals(testBody, requestBodies["product"])
	}

}
