package com.ingi

sealed trait TextElement {
  def content: String
  def styleMap: Map[String, String]
}

case class Word(content: String, styleMap: Map[String, String]) extends TextElement
case class Punctuation(content: String, styleMap: Map[String, String]) extends TextElement