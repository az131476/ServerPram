using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;

namespace MachineFill
{
    class CoderQueue
    {
        public static Queue ckSyncdQueue = Queue.Synchronized(new Queue());
        public static Queue fdSyncdQueue = Queue.Synchronized(new Queue());
    }
}
