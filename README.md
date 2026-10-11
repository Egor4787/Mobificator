# Mobificator
By [Egor4787](https://modrinth.com/user/egor4787/) • Minecraft 26.2

<a href="https://modrinth.com/mod/mobificator"><img src="https://raw.githubusercontent.com/jvqtil/md-badges/refs/heads/master/.github/assets/badge-modrinth.png" height=60></a>
<a href="https://github.com/Egor4787/Mobificator/releases/latest"><img src="https://raw.githubusercontent.com/jvqtil/md-badges/refs/heads/master/.github/assets/badge-github.png" height=56></a>

### Simplify the long commands. Do it faster

<img src="https://github.com/Egor4787/Mobificator/blob/master/.github/assets/screenshots/banner.png?raw=true" height=250 alt="Mobificator Banner">

This mod allows you to quickly change attributes, name of **mobs**, **players** and **yourself**. Has **value preview**, **suggestions** while typing.

Modes: 
- **Instant** - `i` bind
- Lab - `m` bind

#### Instant mode
Look at a mob or a player to take it as target. Otherwise, **you** are the target.

Syntax is very simple, even a toddler could get it.<br>
`<attribute> <value>` to set a value or `<attribute> reset` to reset it to the default value.<br>
`name <value` to change name (doesn't work on players)

Here's an example:<br>
You send `scale 2`, your scale multiplier value becomes `2`. Simple as that.

### How does it work?
After pressing enter key, what does it do?
> Simply sends the command to chat. This way it works on both single-player worlds and servers. No overcomplexity.

For example, when you type `max_health 10` and press enter, it will be converted to `/attribute <target> max_health base set 10`.

See, you don't have to type the full command, and you can get the value immediately, without doing `/attribute <mob> max_health base get`.

#### P.S.
If you enjoy the mod, please follow it on Modrinth or give a star on GitHub. Contributions are always welcome too!
