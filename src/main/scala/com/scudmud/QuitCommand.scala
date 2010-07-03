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

/**
 * A command used for when the user wants to disconnect from the server.
 */
class QuitCommand extends Command {
  /**
   * Execute the quit command ignoring any arguments given.
   * @param args the arguments given for the command, ignored by this command
   * @param p    the player executing this command
   * @param a    the actor to send the delta to
   */
  def execute(args: String, p: Player): java.util.List[Delta] = {
    val list = new java.util.ArrayList[Delta]()
    list.add(new PlayerQuitDelta(p))
    list
  }

  /**
   * Return the command's identifier.
   * @return quit command's identifier is "quit"
   */
  override def toString() = {
    "quit"
  }
}
