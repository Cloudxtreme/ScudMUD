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

import scala.collection.mutable.Map
import scala.actors._
import Actor._
import java.nio.channels._

/**
 * The main static class that controls the heartbeat and keeps everything in 
 * sync.
 */
object ScudMUD {
  var players = Map[SelectionKey, Player]()
  val globalRoom = new Room()
  
  /**
   * The entry point for the program.
   * @param args the command line arguments the server receives
   */
  def main(args: Array[String]): Unit = {
    CommandHandler initExecutors (Runtime.getRuntime.availableProcessors*2)
    val say = new SayCommand
    val quit = new QuitCommand
    CommandHandler addCommand say
    CommandHandler addCommand quit
    NetworkManager // Start listening for clients
    heartBeatLoop()  
    0 
  }

  /**
   * Apply the list of deltas to the data structures.
   * @param deltas the changes to apply to the data structures
   */
  def applyDeltas(deltas: List[Delta]) {
    for(delta <- deltas) {
      delta.applyChanges
    }
  }

  /**
   * The heart beat loop that ticks every manager once per turn, at the very 
   * start of the turn.
   */
  def heartBeatLoop() {
    var continue = true
    while(continue) {
      val startTime = System.currentTimeMillis()

      val messages = NetworkManager.selectNow(players)

      // Pass messages to command handler.  No changes to any objects allowed
      // in the command handler.  Need to be extremely careful to be sure that
      // this never happens.  Deltas allow changes to be "planned" without 
      // having to have them be done in the concurrent parts of the program. 
      val deltas = CommandHandler.execute(messages, players.values.toList) 

      // Apply deltas if we have any.
      if(deltas.size>0)
        applyDeltas(deltas)
 
      val diff = System.currentTimeMillis() - startTime
      if(diff<100) {
        println("Diff: " + (100-diff) + " milliseconds " + Thread.currentThread)
        Thread.sleep(100-diff) 
      } else if(diff>100) {
        println("WARNING: diff>100, something is taking way too long!")
      }
    }
  }
}