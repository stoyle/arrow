package kategory

@instance(ObservableKW::class)
interface ObservableKWMonadInstance :
        ObservableKWApplicativeInstance,
        Monad<ObservableKWHK> {
    override fun <A, B> ap(fa: ObservableKWKind<A>, ff: ObservableKWKind<(A) -> B>): ObservableKW<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: ObservableKWKind<A>, f: (A) -> ObservableKWKind<B>): ObservableKW<B> =
            fa.ev().flatMap { f(it).ev() }

    override fun <A, B> tailRecM(a: A, f: (A) -> ObservableKWKind<Either<A, B>>): ObservableKW<B> =
            f(a).ev().flatMap {
                it.fold({ tailRecM(a, f).ev() }, { pure(it).ev() })
            }
}

@instance(ObservableKW::class)
interface ObservableKWMonadErrorInstance :
        ObservableKWMonadInstance,
        MonadError<ObservableKWHK, Throwable> {
    override fun <A> raiseError(e: Throwable): ObservableKW<A> =
            ObservableKW.raiseError(e)

    override fun <A> handleErrorWith(fa: ObservableKWKind<A>, f: (Throwable) -> ObservableKWKind<A>): ObservableKW<A> =
            fa.ev().handleErrorWith { f(it).ev() }
}