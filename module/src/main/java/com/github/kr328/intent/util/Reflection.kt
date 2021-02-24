package com.github.kr328.intent.util

import java.lang.reflect.Proxy
import java.lang.reflect.Constructor as JConstructor
import java.lang.reflect.Field as JField
import java.lang.reflect.Method as JMethod

annotation class Field(val name: String)
annotation class Constructor

@Suppress("UNCHECKED_CAST")
fun <T> Any.useAs(definition: Class<T>, includePrivate: Boolean = false): T {
    return Proxy.newProxyInstance(definition.classLoader, arrayOf(definition)) { _, method, args ->
        val m = cache.getOrPut(method, ::hashMapOf).getOrPut(javaClass) {
            val clazz: Class<*> = if (this is Class<*>) this else javaClass
            val field = method.getAnnotation(Field::class.java)
            val constructor = method.getAnnotation(Constructor::class.java)

            when {
                field != null -> {
                    val f = if (includePrivate) {
                        clazz.getDeepDeclaredField(field.name)
                    } else {
                        clazz.getField(field.name)
                    }

                    if (method.parameterTypes.isEmpty() && method.returnType == f.type) {
                        InvokeHandler.GetField(f)
                    } else if (method.parameterTypes.size == 1 && method.parameterTypes[0] == f.type) {
                        InvokeHandler.SetField(f)
                    } else {
                        throw NoSuchFieldException("unsupported field method: $method")
                    }
                }
                constructor != null -> {
                    val c = if (includePrivate) {
                        clazz.getDeepDeclaredConstructor(method.parameterTypes)
                    } else {
                        clazz.getConstructor(*method.parameterTypes)
                    }

                    InvokeHandler.NewInstance(c)
                }
                else -> {
                    val m = if (includePrivate) {
                        clazz.getDeepDeclaredMethod(method.name, method.parameterTypes)
                    } else {
                        clazz.getMethod(method.name, *method.parameterTypes)
                    }

                    if (m.returnType != method.returnType)
                        throw NoSuchMethodException("Return type ${m.returnType} not matched")

                    InvokeHandler.CallMethod(m)
                }
            }
        }

        m(if (this is Class<*>) null else this, args ?: emptyArray())
    } as T
}

private interface InvokeHandler {
    operator fun invoke(obj: Any?, args: Array<Any?>): Any?

    class CallMethod(val method: JMethod) : InvokeHandler {
        override fun invoke(obj: Any?, args: Array<Any?>): Any? {
            return method.invoke(obj, *args)
        }
    }

    class GetField(val field: JField) : InvokeHandler {
        override fun invoke(obj: Any?, args: Array<Any?>): Any? {
            return field[obj]
        }
    }

    class SetField(val field: JField) : InvokeHandler {
        override fun invoke(obj: Any?, args: Array<Any?>): Any? {
            field[obj] = args[0]

            return null
        }
    }

    class NewInstance(val constructor: JConstructor<*>) : InvokeHandler {
        override fun invoke(obj: Any?, args: Array<Any?>): Any? {
            return constructor.newInstance(*args)
        }
    }
}

private var cache: HashMap<JMethod, HashMap<Class<*>, InvokeHandler>> = hashMapOf()

private fun Class<*>.getDeepDeclaredConstructor(parameters: Array<Class<*>>): JConstructor<*> {
    return getDeclaredConstructor(*parameters).apply {
        isAccessible = true
    }
}

private fun Class<*>.getDeepDeclaredMethod(name: String, parameters: Array<Class<*>>): JMethod {
    var clazz: Class<*>? = this

    while (clazz != null) {
        try {
            return getDeclaredMethod(name, *parameters).apply { isAccessible = true }
        } catch (e: Exception) {
            // ignore
        }

        clazz = clazz.superclass
    }

    throw NoSuchMethodException("$name(${parameters.joinToString(",")}) not found")
}

private fun Class<*>.getDeepDeclaredField(name: String): JField {
    var clazz: Class<*>? = this

    while (clazz != null) {
        try {
            return getDeclaredField(name).apply { isAccessible = true }
        } catch (e: Exception) {
            // ignore
        }

        clazz = clazz.superclass
    }

    throw NoSuchFieldException("$name not found")
}