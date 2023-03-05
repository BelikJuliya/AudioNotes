package com.example.data.record.db

interface Item<T> {

    fun toDomainObject(): T

}