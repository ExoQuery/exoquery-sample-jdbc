

















  val q =
    (for {
      p <- people
      a <- addresses.join { a -> p.id == a.ownerId }
    } yield (p, a)
    ).groupBy { case (p, a) => 
        (p.firstName, p.lastName) 
    }.map { case (key, group) =>
        (
            key._1, key._2, 
            group.map(_._1.age).avg,
            group.map(_._2.zip).count
        )
    }.sortBy { case (firstName, lastName, _, _) => 
        (firstName -> Asc, lastName -> Desc)
    }




















