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
 * A command is a command that a user can invoke to generate some sort of
 * result.
 */
trait Command {
  /**
   * Execute the command and return deltas and a done message to the actor.
   * @param args the arguments given for the command
   * @param p    the player executing this command
   * @param a    the actor to give deltas and the done message to
   */
  def execute(args: String, p: Player): java.util.List[Delta]
 
  /**
   * Give the command's name that is used to identify it for when a user wants
   * to execute it.
   * @return the command's identification string for execution
   */
  override def toString(): String  
}
