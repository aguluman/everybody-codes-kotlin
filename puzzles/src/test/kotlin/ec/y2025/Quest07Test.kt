package ec.y2025

import ec.Answer
import kotlin.test.Test
import kotlin.test.assertEquals

class Quest07Test {

    private val quest = Quest07()

    @Test
    fun `part 1 sample`() {
        val sample = """
            Oronris,Urakris,Oroneth,Uraketh

            r > a,i,o
            i > p,w
            n > e,r
            o > n,m
            k > f,r
            a > k
            U > r
            e > t
            O > r
            t > h
        """.trimIndent()
        assertEquals(Answer("Oroneth"), quest.part1(sample))
    }

    @Test
    fun `part 2 sample`() {
        val sample = """
            Xanverax,Khargyth,Nexzeth,Helther,Braerex,Tirgryph,Kharverax

            r > v,e,a,g,y
            a > e,v,x,r
            e > r,x,v,t
            h > a,e,v
            g > r,y
            y > p,t
            i > v,r
            K > h
            v > e
            B > r
            t > h
            N > e
            p > h
            H > e
            l > t
            z > e
            X > a
            n > v
            x > z
            T > i
        """.trimIndent()
        assertEquals(Answer(23), quest.part2(sample))
    }

    @Test
    fun `part 3 samples`() {
        val small = """
            Xaryt

            X > a,o
            a > r,t
            r > y,e,a
            h > a,e,v
            t > h
            v > e
            y > p,t
        """.trimIndent()
        assertEquals(Answer(25), quest.part3(small))

        // Kharax extends Khara, so its names are counted once; Noxer breaks N > e and is skipped.
        val bigger = """
            Khara,Xaryt,Noxer,Kharax

            r > v,e,a,g,y
            a > e,v,x,r,g
            e > r,x,v,t
            h > a,e,v
            g > r,y
            y > p,t
            i > v,r
            K > h
            v > e
            B > r
            t > h
            N > e
            p > h
            H > e
            l > t
            z > e
            X > a
            n > v
            x > z
            T > i
        """.trimIndent()
        assertEquals(Answer(1154), quest.part3(bigger))
    }
}