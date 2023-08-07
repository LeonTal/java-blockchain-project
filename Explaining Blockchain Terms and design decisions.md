Compared to a real Blockchain, my one has a few changes to make it more testable. For example, a 
normal Blockchain would have thousands of blocks "solved" before the reward given to the users 
would be lowered. Mine, however, gets lowered within a few solves. 

Another change is that my blocks are usually solved within a few seconds, much quicker than a regular
proof of work blockchain can. Again, for testing purposes, as waiting weeks, months, or even years for
a single block to be solved isn't exactly logical for testing purposes.

Depending on the type of blockchain being used, the blocks within the blockchain are made up of
multiple parts. The blocks in the Bitcoin blockchain are made up of six main parts (excluding the
transaction list). These six parts are hashed together in order to obtain a hash which is then converted
to decimal and compared to the current target goal to see if it is lower or equal to the current goal, if it
is not lower or equal, it will continue going through hashes until it finds one that meets those
requirements.

The six parts of a Bitcoin block are as follows.
1. The previous block hash which looks at the last block in the blockchain and takes the overall
hash for that block. This is the part of a block that creates the blockchain as it causes each
block to be linked to the previous block, forming a chain.
2. The current version of the blockchain
3. The nonce which is one of the two values in a block that is constantly changing. The nonce
gets constantly incremented and then hashed with the rest of the values to obtain a hash, this
hash is then compared with the nBits to see if the current hash is lower or equal to it, if not, it
continues incrementing the nonce. The nBits is the target goal for the hash to reach. The nBits is
constantly changed during the lifespan of the blockchain and is either made easier to solve or more
difficult to solve depending on how quickly the last X blocks.
4. The current time and date is the other value that constantly changes every second.
5. The merkle root is the result of hashing transactions together in pairs and continuing to do
that until the last transaction that will be placed in the block is reached. Once that point is
reached, that hash will be placed as the merkle root for that block
6. Finally, the target hash is what the overall hash of the current block needs to be lower than or
equal to in order to class as a solved block
