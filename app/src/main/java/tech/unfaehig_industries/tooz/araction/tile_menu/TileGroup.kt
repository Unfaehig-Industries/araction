package tech.unfaehig_industries.tooz.araction.tile_menu

class TileGroup {

    var parent: TileButton
    var children: ArrayList<TileGroup>? = null
    var descendants: ArrayList<TileButton>

    constructor(_parent: TileButton, _children: ArrayList<TileGroup>?) {
        parent = _parent
        children = _children

        descendants = unpackChildren()
    }

    private fun unpackChildren(): ArrayList<TileButton> {
        val unpackedDescendants: ArrayList<TileButton> = arrayListOf(parent)

        children?.let {
            for(child in it) {
                unpackedDescendants.addAll(child.unpackChildren())
            }
        }

        return unpackedDescendants
    }

    fun findChild(button: TileButton, ancestors: ArrayList<TileButton>): ArrayList<TileButton> {
        children?.let {
            for (child in it) {
                val ancestorsWithParent: ArrayList<TileButton> = arrayListOf()
                ancestorsWithParent.addAll(ancestors)

                if(child.parent == button) return ancestorsWithParent

                return findChild(button, ancestorsWithParent)
            }
        }

        throw Exception("Could not find button")
    }
}