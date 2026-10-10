# Mobificator
By [Egor4787](https://modrinth.com/user/egor4787/) • Minecraft 26.2

<a href="https://modrinth.com/mod/mobificator"><img src="https://raw.githubusercontent.com/jvqtil/md-badges/refs/heads/master/.github/assets/badge-modrinth.png" height=60></a>
<a href="https://github.com/Egor4787/Mobificator/releases/latest"><img src="https://raw.githubusercontent.com/jvqtil/md-badges/refs/heads/master/.github/assets/badge-github.png" height=56></a>

### Minecraft mod that allows you to quickly control any living entities. Mobs, players, self
This mod allows you to quickly change attributes of a mob or a player by just looking at it and pressing `i` for Instant mode and `m` for Lab mode.

Also works on self. To open self-control Instant mode, press the same `i` key while not looking at any other entity (mobs, players).

In the Instant mode, use this syntax
`<attribute> <value>` to set a value or `<attribute> reset` to reset it to the default value.

Instant mode also has suggestions while typing. Those show the full attribute name with the current value of an attribute.

### How does it work?
After pressing enter key, what does it do?
> Simply sends the command to chat. This way it works on both single-play worlds and servers. No overcomplexity.

For example, if you type `max_health 10` and press enter, this will be converted to `/attribute <mob> max_health base set 10`.

See, you don't have to type the full command, and you can get the value immediately, without doing `/attribute <mob> max_health base get`.

#### P.S.
If you enjoy the mod, please follow it on Modrinth or give a star on GitHub. Contributions are always welcome too!