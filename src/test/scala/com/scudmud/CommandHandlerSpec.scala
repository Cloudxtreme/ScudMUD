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

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers

/**
 * Test to make sure the command handler works correctly.
 */
class CommandHandlerSpec extends Spec with MustMatchers with MavenTrick {
  describe("The command handler") {
    it("should return a room's command over a global command when getCommand" +
	" is called") {
      val r = new Room
      val sayRoom = new SayCommand
      r addCommand sayRoom
      val sayGlobal = new SayCommand
      CommandHandler addCommand sayGlobal
      // Generate a player with nulls as we don't need real data to test quit.
      val p = new Player(null, r)
      assert(CommandHandler.getCommand(p, "say") eq sayRoom)
    }
  }
}
