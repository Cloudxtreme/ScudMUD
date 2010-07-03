require 'java'

import com.scudmud.Command

class SayCommand
  include Command
  def execute(args, p)
    p.room.sendMessage(p, p.key.toString() + " says \"" + args + "\"\n")
    p.sendMessage("You say \"" + args + "\"\n")

    return Array.new
  end

  def getPrefix() 
    return "say"
  end
end

$command = SayCommand.new
