/*
 * Copyright (c) 2010, Preston Skupinski <skupinsk@cse.msu.edu>
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package com.scudmud

import scala.actors._
import Actor._
import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers

class QuitCommandSpec extends Spec with MustMatchers with MavenTrick {
  describe("The quit command when executed") {
    it("should generate a PlayerQuitDelta for the player") {
      val q = new QuitCommand()
      val p = new Player(null)
      q.execute("", p, self)

      receiveWithin(50) {
        case DoneWithDelta(d) => 
          assert(d.getClass.getName=="com.scudmud.PlayerQuitDelta")
      }    
    }
  }
}