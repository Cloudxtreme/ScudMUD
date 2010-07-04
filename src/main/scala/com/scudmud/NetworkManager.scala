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
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE
 */ 
package com.scudmud

import scala.collection.mutable.Map
import java.nio.channels._
import java.nio._
import java.nio.charset._
import java.io.IOException

/**
 * The network manager.
 */
object NetworkManager {
  val charset = Charset.forName("ISO-8859-1")
  val encoder = charset.newEncoder
  val decoder = charset.newDecoder
  val selector = Selector.open
  val server = ServerSocketChannel.open
  server.socket.bind(new java.net.InetSocketAddress(8988))
  server.configureBlocking(false)
  val serverKey = server.register(selector, SelectionKey.OP_ACCEPT)
  val buffer = ByteBuffer.allocate(1024)

  /** 
   * Send a message to the client with the given selection key.
   * @param s   the selection key used to identify who to send the message to
   * @param str the message to be sent
   */
  def write(s: SelectionKey, str: String) {
    val chan = s.channel.asInstanceOf[SocketChannel]
    try {
      chan.write(encoder.encode(CharBuffer.wrap(str)))
    } catch {
      case ex: Exception =>
    }
  }

  /**
   * Disconnect the network connection for the user with the given selection
   * key.
   * @param s the selection key representing the user to disconnect
   */
  def cancelKey(s: SelectionKey) {
    val client = s.channel.asInstanceOf[SocketChannel]
    client.close
    s.cancel
  }

  /**
   * Splits up the request as multiple requests can be sent during a single 
   * turn.  It will also be able to handle telnet protocol separate from user
   * messages.
   * @param request the message received from the user to be split
   */
  def splitRequest(request: String) = {
    // If multiple messages made it in during the turn then split them up into
    // separate messages.  We preserve the carriage return and split on "\n"
    // because lines are ended with "\r\n".  Lines will end in \r with how this
    // is split and a message containing just \r means that the user only hit
    // enter for their message.  Example of why this is necessary: Say
    // "\r\n\r\nquit\r\n" is received during the course of one turn, to
    // properly handle that we would need to send two new prompts and then
    // execute the quit command. This function is so simple mainly because it
    // doesn't separate telnet protocol stuff yet.
    request.split("""\n""")
  }

  /**
   * Have the selectors select keys now and pull all messages received from 
   * users since the last selection.
   * @param players a map used for finding players based on selection keys
   * @return        a list of tuples containing the messages each player sent
   */ 
  def selectNow(players: Map[SelectionKey, Player]) = {
    selector.selectNow
    val keys = selector.selectedKeys
    var messages = List[(Player, String)]()
    val iterator = keys.iterator
    while(iterator.hasNext) {
      val key = iterator.next
      if(key eq serverKey) {
        if(key.isAcceptable) {
          val client = server.accept
          if(client ne null)  {
            client.configureBlocking(false)
            val clientKey = client.register(selector, SelectionKey.OP_READ)
            val p = new Player(clientKey, ScudMUD.globalRoom)
            players(clientKey) = p
            ScudMUD.globalRoom.players+=p
            p.sendMessage("> ")
          }
        }
      } else {
        val client = key.channel.asInstanceOf[SocketChannel]
        try {
          if(key.isReadable()) {
            val numRead = client.read(buffer)

            if(numRead != -1) {
              buffer.flip
              val request = decoder.decode(buffer).toString()
              buffer.clear
              // Discard worthless messsages.
              if(request.size!=0) {
                splitRequest(request).foreach { str =>
                  messages += (players(key), str)
                } 
              }
            } else {
              client.close 
              key.cancel
              // TODO: Clean up client disconnection through EOF.
              players removeKey key
            }
          }
        } catch {
          case ex: CancelledKeyException => // Ignore it.
          case ex: ClosedChannelException => // Ignore it.
        }
      }         
    }
    messages
  }
}
