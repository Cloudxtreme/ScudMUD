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
import javax.script._
import java.io.File

/**
 * The main static class that controls the heartbeat and keeps everything in 
 * sync.
 */
object ScudMUD {
  /**
   * A map of players for finding a player based on their selection key.
   */
  var players = Map[SelectionKey, Player]()

  /**
   * The global room, the only room available while I work out things and get
   * dynamic room creation set up.
   */
  val globalRoom = new Room()

  /**
   * The name to player map for finding a player based on a given name.
   */
  var nameToPlayer = Map[String, Player]()
  
  /**
   * The entry point for the program.
   * @param args the command line arguments the server receives
   */
  def main(args: Array[String]): Unit = {
    CommandHandler initExecutors (Runtime.getRuntime.availableProcessors*2)

    val pluginDir = new File("scripts/commands")
    val scriptCommands = loadScriptCommands(pluginDir)  
   
    scriptCommands.foreach { command =>  CommandHandler addCommand command }
    //val say = new SayCommand
    val quit = new QuitCommand
    //CommandHandler addCommand say
    CommandHandler addCommand quit
    NetworkManager // Start listening for clients
    heartBeatLoop()  
    0 
  }

  def loadScriptCommands(commandDir: File): List[Command] = {
    val manager = new ScriptEngineManager
    var commands = List[Command]()
    commandDir.listFiles().foreach { file =>
      val name = file.getName
      val ext = name.substring(name.indexOf(".")+1, name.length)
      val engine = manager.getEngineByExtension(ext)
      engine.eval(new java.io.FileReader(file.getAbsolutePath))
      commands += engine.get("command").asInstanceOf[Command]
    }
    commands 
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

      // Tick anything that would use heartbeat ticks as a form of currency
      // for various actions here.

      // Get messages from the network manager sent by players.
      val messages = NetworkManager.selectNow(players)

      // Pass messages to command handler.  No changes to any objects allowed
      // in the command handler.  Need to be extremely careful to be sure that
      // this never happens.  Deltas allow changes to be "planned" without 
      // having to have them be done in the concurrent parts of the program. 
      val deltas = CommandHandler.execute(messages)

      // Apply deltas if we have any.  Database interaction will be done as 
      // changes occur in the deltas unless I figure out some better way of 
      // going about it that isn't way too complicated.
      if(deltas.size>0)
        applyDeltas(deltas)

      // Sleep the duration of the turn if the turn has not ended yet.
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
