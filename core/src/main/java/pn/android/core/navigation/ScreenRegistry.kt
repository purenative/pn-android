package pn.android.core.navigation

import com.ramcosta.composedestinations.spec.Direction
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

interface ScreenProvider

private typealias ProviderKey = KClass<out ScreenProvider>
private typealias ScreenFactory = (ScreenProvider) -> Direction
private typealias ScreenModule = ScreenRegistry.() -> Unit

fun screenModule(block: ScreenModule): ScreenModule =
    { block() }

object ScreenRegistry {

    @PublishedApi
    internal val factories: ConcurrentHashMap<ProviderKey, ScreenFactory> =
        ConcurrentHashMap<ProviderKey, ScreenFactory>()

    operator fun invoke(block: ScreenRegistry.() -> Unit) {
        this.block()
    }

    inline fun <reified T : ScreenProvider> register(noinline factory: (T) -> Direction) {
        factories[T::class] = factory as ScreenFactory
    }

    fun get(provider: ScreenProvider): Direction {
        val factory = factories[provider::class]
            ?: error("ScreenProvider not registered: ${provider::class.qualifiedName}")
        return factory(provider)
    }

}