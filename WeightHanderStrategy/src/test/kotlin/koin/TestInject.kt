package koin

import lin.config.ConfigDispatcher
import lin.config.handler.ConfigHandler
import lin.config.handler.UseConfigHandler
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class A {
    fun sayHello() {
        println("Hello")
    }
}

class TestInject : KoinComponent {
    @BeforeTest
    fun install() {
        startKoin {
            modules(module {
                singleOf(::UseConfigHandler) bind ConfigHandler::class
                single<ConfigDispatcher> { ConfigDispatcher(getAll()) }
            })
        }
    }

    @AfterTest
    fun stop() {
        stopKoin()
    }

    @Test
    fun test1() {
        val a = inject<A>(A::class.java).value
        a.sayHello()
    }

    @Test
    fun test2() {
        val a: A by inject()
        a.sayHello()
    }

    @Test
    fun test3() {
        val configDispatcher = get<ConfigDispatcher>()
        println(configDispatcher)
    }

}