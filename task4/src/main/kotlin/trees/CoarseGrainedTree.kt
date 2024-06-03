package trees
import kotlinx.coroutines.sync.Mutex

class CoarseGrainedTree<T : Comparable<T>> : BSTree<T>() {

    private val mutex = Mutex()

    override suspend fun add(data: T) {
        mutex.lock()
        super.add(data)
        mutex.unlock()
    }

    override suspend fun contains(data: T): Boolean {
        mutex.lock()
        val res = super.contains(data)
        mutex.unlock()
        return  res
    }

    override suspend fun delete(data: T) {
        mutex.lock()
        super.delete(data)
        mutex.unlock()
    }

    override suspend fun preOrder(): List<BSNode<T>> {
        mutex.lock()
        val res = super.preOrder()
        mutex.unlock()
        return res
    }
}